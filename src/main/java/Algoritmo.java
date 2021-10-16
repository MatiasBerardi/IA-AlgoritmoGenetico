import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import java.io.IOException;
import java.util.stream.Collectors;

public class Algoritmo
{
    public static void main(String[] args)
    {
        GetPropertyValues propertyValues = new GetPropertyValues();
        double p = 0.5;

        try {
            p = propertyValues.getPropValues();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Factory<Genotype<BitGene>> gtf = Genotype.of(
                BitChromosome.of(6, p),
                BitChromosome.of(6, p),
                BitChromosome.of(6, p)
        );

        Engine<BitGene, Integer> engine = Engine.builder(Algoritmo::eval, gtf).build();

        Genotype<BitGene> result = engine.stream().limit(200).collect(EvolutionResult.toBestGenotype());

        System.out.println("Resultado " + result);
    }

    private static Integer eval(Genotype<BitGene> gt)
    {
        Integer result = 0;

        if(GenInvalido(gt))
            result -= 30;

        if(PizzasRepetidas(gt))
            result -= 5;

        if(FamiliasRepetidas(gt))
            result -= 5;

        if(PisosRepetidas(gt))
            result -= 5;

        //Propina(Romero) != 1.5 [Posicion 0 del cromosoma = 1.5]
        if(!getFamilia((BitChromosome) gt.getChromosome(0)).equals("10"))
            result += 10;

        //Propina(Morales) != 0.5 [Posicion 2 del cromosoma = 0.5]
        if(!getFamilia((BitChromosome) gt.getChromosome(2)).equals("01"))
            result += 10;

        return  result;
    }

    private static boolean GenInvalido(Genotype<BitGene> gen)
    {
        return gen.stream().map(c ->
        {
            String pizza = getPizza((BitChromosome) c);
            String familia = getFamilia((BitChromosome) c);
            String piso = getPiso((BitChromosome) c);

            return (pizza.equals("11") || familia.equals("11") || piso.equals("11"));

        }).collect(Collectors.toList()).contains(true);
    }

    private static boolean PizzasRepetidas(Genotype<BitGene> gen)
    {
        String p1 = getPizza((BitChromosome) gen.getChromosome(0));
        String p2 = getPizza((BitChromosome) gen.getChromosome(1));
        String p3 = getPizza((BitChromosome) gen.getChromosome(2));

        return (p1.equals(p2) || p1.equals(p3) || p2.equals(p3));
    }

    private static boolean FamiliasRepetidas(Genotype<BitGene> gen)
    {
        String f1 = getFamilia((BitChromosome) gen.getChromosome(0));
        String f2 = getFamilia((BitChromosome) gen.getChromosome(1));
        String f3 = getFamilia((BitChromosome) gen.getChromosome(2));

        return (f1.equals(f2) || f1.equals(f3) || f2.equals(f3));
    }

    private static boolean PisosRepetidas(Genotype<BitGene> gen)
    {
        String p1 = getPiso((BitChromosome) gen.getChromosome(0));
        String p2 = getPiso((BitChromosome) gen.getChromosome(1));
        String p3 = getPiso((BitChromosome) gen.getChromosome(2));

        return (p1.equals(p2) || p1.equals(p3) || p2.equals(p3));
    }

    private static String getPizza(BitChromosome cromosoma)
    {
        return cromosoma.toString().substring(2, 4);
    }

    private static String getFamilia(BitChromosome cromosoma)
    {
        return cromosoma.toString().substring(4, 6);
    }

    private static String getPiso(BitChromosome cromosoma)
    {
        return cromosoma.toString().substring(6, 8);
    }

    private static double getPropina(String familia, Genotype<BitGene> gen)
    {
        int pos = 0;
        for(int i = 0; i < 3; i++)
        {
            if(getFamilia((BitChromosome) gen.getChromosome(i)).equals(familia))
                pos = i;
        }

        switch (pos)
        {
            case 0:
                return 1.5;
            case 1:
                return 1.0;
            case 2:
                return 0.5;
        }

        return -1;
    }
}
