import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Algoritmo {
    final static String anchoas = "00";
    final static String morrones = "01";
    final static String palmitos = "10";

    final static String piso2 = "00";
    final static String piso5 = "01";
    final static String piso8 = "10";

    final static String franco = "00";
    final static String morales = "01";
    final static String romero = "10";


    public static void main(String[] args) {
        while (true) {
            GetPropertyValues propertyValues = new GetPropertyValues();
            Scanner sc = new Scanner(System.in);
            System.out.println("Ingrese cantidad de corridas: ");
            int corridas = sc.nextInt();
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

            Genotype<BitGene> result = engine.stream().limit(corridas).collect(EvolutionResult.toBestGenotype());

            System.out.println("Resultado " + result);
        }
    }

    private static Integer eval(Genotype<BitGene> gt) {
        Integer result = 0;

        if (GenInvalido(gt))
            result -= 30;

        if (PizzasRepetidas(gt))
            result -= 5;

        if (FamiliasRepetidas(gt))
            result -= 5;

        if (PisosRepetidas(gt))
            result -= 5;

        //Propina(Romero) != 1.5 [Posicion 0 del cromosoma = 1.5]
        if (!getFamilia((BitChromosome) gt.getChromosome(0)).equals(romero))
            result += 10;

        //Propina(Morales) != 0.5 [Posicion 2 del cromosoma = 0.5]
        if (!getFamilia((BitChromosome) gt.getChromosome(2)).equals(morales))
            result += 10;

        //Piso(Romero) != 2
        if (!getPisoforFamilia(romero, gt).equals(piso2))
            result += 10;

        //Piso(Morales) != 8
        if (!getPisoforFamilia(morales, gt).equals(piso8))
            result += 10;

        //Piso(Franco) != 2
        if (!getPisoforFamilia(franco, gt).equals(piso2))
            result += 10;

        //Pizza(Morales) != Anchoas
        if (!getPizzaforFamilia(morales, gt).equals(anchoas))
            result += 10;

        //Pizza(Franco) != Palmitos
        if (!getPizzaforFamilia(franco, gt).equals(palmitos))
            result += 10;

        //Propina(Morrones) != 1.5 [Posicion 0 del cromosoma = 0.5]
        if (!getPizza((BitChromosome) gt.getChromosome(0)).equals(morrones))
            result += 10;

        //Pizza(2) = Palmito
        if (!getPizzaforPiso(piso2, gt).equals(palmitos))
            result += 10;

        //Piso(1.5) != 8 [Posicion 0 del cromosoma = 1.5]
        if (!getPiso((BitChromosome) gt.getChromosome(0)).equals(piso8))
            result += 10;

        //Pizza(8) != Anchoas
        if (!getPizzaforPiso(piso8, gt).equals(anchoas))
            result += 10;

        //Propina(5) != 0.5 [Posicion 2 del cromosoma = 0.5]
        if (!getPiso((BitChromosome) gt.getChromosome(2)).equals(piso5))
            result += 10;

        //Propina(Morrones) = Propina(5) - 0.5
        if (getPropinaforPizza(morrones, gt) == (getPropinaforPiso(piso5, gt) - 0.5))
            result += 10;

        //Piso(Romero) = Piso(1.5) + 3
        if (getPisoValueforFamilia(romero, gt) == (getPisoValueForPropina((BitChromosome) gt.getChromosome(0)) + 3))
            result += 10;

        //Propina(Morales) = Propina(Romero) + 0.5
        if (getPropinaforFamilia(morales, gt) == (getPropinaforFamilia(romero, gt) + 0.5))
            result += 10;


        return result;
    }

    private static boolean GenInvalido(Genotype<BitGene> gen) {
        return gen.stream().map(c ->
        {
            String pizza = getPizza((BitChromosome) c);
            String familia = getFamilia((BitChromosome) c);
            String piso = getPiso((BitChromosome) c);

            return (pizza.equals("11") || familia.equals("11") || piso.equals("11"));

        }).collect(Collectors.toList()).contains(true);
    }

    private static boolean PizzasRepetidas(Genotype<BitGene> gen) {
        String p1 = getPizza((BitChromosome) gen.getChromosome(0));
        String p2 = getPizza((BitChromosome) gen.getChromosome(1));
        String p3 = getPizza((BitChromosome) gen.getChromosome(2));

        return (p1.equals(p2) || p1.equals(p3) || p2.equals(p3));
    }

    private static boolean FamiliasRepetidas(Genotype<BitGene> gen) {
        String f1 = getFamilia((BitChromosome) gen.getChromosome(0));
        String f2 = getFamilia((BitChromosome) gen.getChromosome(1));
        String f3 = getFamilia((BitChromosome) gen.getChromosome(2));

        return (f1.equals(f2) || f1.equals(f3) || f2.equals(f3));
    }

    private static boolean PisosRepetidas(Genotype<BitGene> gen) {
        String p1 = getPiso((BitChromosome) gen.getChromosome(0));
        String p2 = getPiso((BitChromosome) gen.getChromosome(1));
        String p3 = getPiso((BitChromosome) gen.getChromosome(2));

        return (p1.equals(p2) || p1.equals(p3) || p2.equals(p3));
    }

    private static String getPisoforFamilia(String familia, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getFamilia((BitChromosome) gen.getChromosome(i)).equals(familia))
                pos = i;
        }
        return getPiso((BitChromosome) gen.getChromosome(pos));
    }

    private static String getPizzaforPiso(String piso, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getPiso((BitChromosome) gen.getChromosome(i)).equals(piso))
                pos = i;
        }
        return getPizza((BitChromosome) gen.getChromosome(pos));
    }

    private static String getPizzaforFamilia(String familia, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getFamilia((BitChromosome) gen.getChromosome(i)).equals(familia))
                pos = i;
        }
        return getPizza((BitChromosome) gen.getChromosome(pos));
    }

    private static String getPizza(BitChromosome cromosoma) {
        return cromosoma.toString().substring(2, 4);
    }

    private static String getFamilia(BitChromosome cromosoma) {
        return cromosoma.toString().substring(4, 6);
    }

    private static String getPiso(BitChromosome cromosoma) {
        return cromosoma.toString().substring(6, 8);
    }

    private static double getPropinaforFamilia(String familia, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getFamilia((BitChromosome) gen.getChromosome(i)).equals(familia))
                pos = i;
        }

        switch (pos) {
            case 0:
                return 1.5;
            case 1:
                return 1.0;
            case 2:
                return 0.5;
        }

        return -1;
    }

    private static double getPropinaforPizza(String pizza, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getPizza((BitChromosome) gen.getChromosome(i)).equals(pizza))
                pos = i;
        }

        switch (pos) {
            case 0:
                return 1.5;
            case 1:
                return 1.0;
            case 2:
                return 0.5;
        }

        return -1;
    }

    private static double getPropinaforPiso(String piso, Genotype<BitGene> gen) {
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            if (getPiso((BitChromosome) gen.getChromosome(i)).equals(piso))
                pos = i;
        }

        switch (pos) {
            case 0:
                return 1.5;
            case 1:
                return 1.0;
            case 2:
                return 0.5;
        }

        return -1;
    }

    private static int getPisoValueforFamilia(String familia, Genotype<BitGene> gen) {
        String piso = getPisoforFamilia(familia, gen);

        if (piso.equals(piso2))
            return 2;

        if (piso.equals(piso5))
            return 5;

        if (piso.equals(piso8))
            return 8;

        return -1;
    }

    private static int getPisoValueForPropina(BitChromosome cromosoma) {
        String piso = getPiso(cromosoma);

        if (piso.equals(piso2))
            return 2;

        if (piso.equals(piso5))
            return 5;

        if (piso.equals(piso8))
            return 8;

        return -1;
    }
}
