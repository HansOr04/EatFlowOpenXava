package com.grupo4.Eatflow.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderDiscountCalculator - Pruebas de Caja Blanca y Caja Negra")
class OrderDiscountCalculatorTest {

    // =========================================================
    // CAJA BLANCA: cobertura de sentencias y decisiones (CB-01..CB-07)
    // =========================================================

    @Test
    @DisplayName("CB-01: totalItems negativo -> lanza IllegalArgumentException")
    void testCalculateFinalAmount_TotalItemsNegativo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderDiscountCalculator.calculateFinalAmount(-1, 100.0));
    }

    @Test
    @DisplayName("CB-02: subtotal negativo -> lanza IllegalArgumentException")
    void testCalculateFinalAmount_SubtotalNegativo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderDiscountCalculator.calculateFinalAmount(3, -10.0));
    }

    @Test
    @DisplayName("CB-03: 20 items -> descuento 15%, retorna 85.0")
    void testCalculateFinalAmount_VeinteItems_AplicaDescuentoQuincePorciento() {
        assertEquals(85.0, OrderDiscountCalculator.calculateFinalAmount(20, 100.0), 0.001);
    }

    @Test
    @DisplayName("CB-04: 10 items -> descuento 10%, retorna 180.0")
    void testCalculateFinalAmount_DiezItems_AplicaDescuentoDiezPorciento() {
        assertEquals(180.0, OrderDiscountCalculator.calculateFinalAmount(10, 200.0), 0.001);
    }

    @Test
    @DisplayName("CB-05: 5 items -> descuento 5%, retorna 38.0")
    void testCalculateFinalAmount_CincoItems_AplicaDescuentoCincoPorciento() {
        assertEquals(38.0, OrderDiscountCalculator.calculateFinalAmount(5, 40.0), 0.001);
    }

    @Test
    @DisplayName("CB-06: 4 items -> sin descuento, retorna subtotal sin modificar: 60.0")
    void testCalculateFinalAmount_CuatroItems_SinDescuento() {
        assertEquals(60.0, OrderDiscountCalculator.calculateFinalAmount(4, 60.0), 0.001);
    }

    @Test
    @DisplayName("CB-07: 0 items con subtotal 0.0 -> retorna 0.0")
    void testCalculateFinalAmount_CeroItems_CeroSubtotal_RetornaCero() {
        assertEquals(0.0, OrderDiscountCalculator.calculateFinalAmount(0, 0.0), 0.001);
    }

    // =========================================================
    // CAJA NEGRA: particion de equivalencia y valores limite (VL-01..VL-06)
    // =========================================================

    @Nested
    @DisplayName("Particion de Equivalencia y Valores Limite")
    class ParticionEquivalenciaYValoresLimite {

        @ParameterizedTest(name = "VL-{index}: totalItems={0}, subtotal={1} -> esperado={2}")
        @DisplayName("Fronteras de descuento: 4/5, 9/10, 19/20")
        @CsvSource({
            "4,  100.0, 100.0",   // VL-01: justo antes del limite 5 -> 0%
            "5,  100.0, 95.0",    // VL-02: frontera exacta de 5%
            "9,  100.0, 95.0",    // VL-03: justo antes del limite 10 -> 5%
            "10, 100.0, 90.0",    // VL-04: frontera exacta de 10%
            "19, 100.0, 90.0",    // VL-05: justo antes del limite 20 -> 10%
            "20, 100.0, 85.0"     // VL-06: frontera exacta de 15%
        })
        void testValoresLimiteDescuento(int totalItems, double subtotal, double esperado) {
            assertEquals(esperado,
                    OrderDiscountCalculator.calculateFinalAmount(totalItems, subtotal),
                    0.001);
        }
    }
}
