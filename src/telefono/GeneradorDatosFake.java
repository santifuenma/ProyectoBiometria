package com.example.sprint0_biometria;

import java.util.Random;

public class GeneradorDatosFake {

    // Se crea un generador de números aleatorios para simular datos
    private static final Random random = new Random();

    // Se genera un identificador de sensor falso
    // En este caso devuelve un número entre 1 y 3
    public static int getFakeSensorId() {
        return random.nextInt(3) + 1;
    }

    // Se genera un valor falso para el sensor
    // En este ejemplo se devuelve un número entre 300 y 800
    public static double getFakeValue() {
        return 300 + (500 * random.nextDouble());
    }
}
