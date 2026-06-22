package com.grupo4.Eatflow.modelo;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openxava.jpa.XPersistence;

import com.grupo4.Eatflow.modelo.enums.OrderStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de Caja Negra sobre Order.closeOrder().
 *
 * ACOPLAMIENTO DETECTADO: closeOrder() llama XPersistence.getManager().merge(this),
 * que requiere un contexto JPA activo. Se usa mockStatic(XPersistence.class) de
 * Mockito 5 (inline mock maker por defecto) para interceptar esa llamada estatica
 * sin modificar la entidad ni levantar un servidor de aplicaciones.
 *
 * Si XPersistence tuviera un inicializador estatico problematico (improbable —
 * es una clase utilitaria con ThreadLocal), los tests R3-R6 fallarian con
 * ExceptionInInitializerError. La alternativa seria extraer la logica de calculo
 * a un metodo package-private en Order y probarlo directamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order - Tabla de Decision closeOrder() y Transiciones de Estado")
class OrderTest {

    private OrderItem crearItem(int cantidad, double precioUnitario) {
        OrderItem item = new OrderItem();
        item.setQuantity(cantidad);
        item.setUnitPrice(BigDecimal.valueOf(precioUnitario));
        return item;
    }

    // =========================================================
    // TAREA 4: TABLA DE DECISION - closeOrder()
    // =========================================================

    @Test
    @DisplayName("R1: totalItems negativo (suma de cantidades < 0) -> lanza IllegalArgumentException")
    void testCloseOrder_TotalItemsNegativo_LanzaExcepcion() {
        Order order = new Order();
        order.getItems().add(crearItem(-1, 10.0)); // totalItems = -1

        // La excepcion se lanza ANTES de llegar a XPersistence.getManager() -> no necesita mock
        assertThrows(IllegalArgumentException.class, order::closeOrder);
    }

    @Test
    @DisplayName("R2: subtotal negativo con totalItems >= 0 -> lanza IllegalArgumentException")
    void testCloseOrder_SubtotalNegativo_LanzaExcepcion() {
        Order order = new Order();
        order.getItems().add(crearItem(1, -5.0)); // totalItems = 1, subtotal = -5.0

        // La excepcion se lanza ANTES de llegar a XPersistence.getManager() -> no necesita mock
        assertThrows(IllegalArgumentException.class, order::closeOrder);
    }

    @Test
    @DisplayName("R3: totalItems en [0,4] -> totalAmount sin descuento (subtotal 60.0 -> 60.0)")
    void testCloseOrder_TotalItemsEntre0y4_SinDescuento() throws Exception {
        Order order = new Order();
        order.getItems().add(crearItem(3, 20.0)); // totalItems=3, subtotal=60.0

        try (MockedStatic<XPersistence> xpMock = mockStatic(XPersistence.class)) {
            EntityManager em = mock(EntityManager.class);
            xpMock.when(XPersistence::getManager).thenReturn(em);

            order.closeOrder();

            assertEquals(60.0, order.getTotalAmount().doubleValue(), 0.001);
        }
    }

    @Test
    @DisplayName("R4: totalItems en [5,9] -> totalAmount con descuento 5% (subtotal 100.0 -> 95.0)")
    void testCloseOrder_TotalItemsEntre5y9_Descuento5Porciento() throws Exception {
        Order order = new Order();
        order.getItems().add(crearItem(5, 20.0)); // totalItems=5, subtotal=100.0

        try (MockedStatic<XPersistence> xpMock = mockStatic(XPersistence.class)) {
            EntityManager em = mock(EntityManager.class);
            xpMock.when(XPersistence::getManager).thenReturn(em);

            order.closeOrder();

            assertEquals(95.0, order.getTotalAmount().doubleValue(), 0.001);
        }
    }

    @Test
    @DisplayName("R5: totalItems en [10,19] -> totalAmount con descuento 10% (subtotal 100.0 -> 90.0)")
    void testCloseOrder_TotalItemsEntre10y19_Descuento10Porciento() throws Exception {
        Order order = new Order();
        order.getItems().add(crearItem(10, 10.0)); // totalItems=10, subtotal=100.0

        try (MockedStatic<XPersistence> xpMock = mockStatic(XPersistence.class)) {
            EntityManager em = mock(EntityManager.class);
            xpMock.when(XPersistence::getManager).thenReturn(em);

            order.closeOrder();

            assertEquals(90.0, order.getTotalAmount().doubleValue(), 0.001);
        }
    }

    @Test
    @DisplayName("R6: totalItems >= 20 -> totalAmount con descuento 15% (subtotal 100.0 -> 85.0)")
    void testCloseOrder_TotalItemsMayorIgual20_Descuento15Porciento() throws Exception {
        Order order = new Order();
        order.getItems().add(crearItem(20, 5.0)); // totalItems=20, subtotal=100.0

        try (MockedStatic<XPersistence> xpMock = mockStatic(XPersistence.class)) {
            EntityManager em = mock(EntityManager.class);
            xpMock.when(XPersistence::getManager).thenReturn(em);

            order.closeOrder();

            assertEquals(85.0, order.getTotalAmount().doubleValue(), 0.001);
        }
    }

    // =========================================================
    // TAREA 5: TRANSICIONES DE ESTADO
    // =========================================================

    @Nested
    @DisplayName("Transiciones de Estado de Order")
    class TransicionesDeEstado {

        @Test
        @DisplayName("PENDING -> IN_PROGRESS: transicion valida, el estado cambia correctamente")
        void testTransicion_PendingAInProgress_Valida() {
            Order order = new Order();
            assertEquals(OrderStatus.PENDING, order.getStatus(),
                    "El estado inicial debe ser PENDING");

            order.setStatus(OrderStatus.IN_PROGRESS);

            assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        }

        @Test
        @DisplayName("PENDING -> CANCELLED: transicion valida, el estado cambia correctamente")
        void testTransicion_PendingACancelled_Valida() {
            Order order = new Order();
            order.setStatus(OrderStatus.CANCELLED);
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
        }

        @Test
        @DisplayName("IN_PROGRESS -> SERVED: transicion valida, el estado cambia correctamente")
        void testTransicion_InProgressAServed_Valida() {
            Order order = new Order();
            order.setStatus(OrderStatus.IN_PROGRESS);
            order.setStatus(OrderStatus.SERVED);
            assertEquals(OrderStatus.SERVED, order.getStatus());
        }

        /**
         * PENDIENTE DE IMPLEMENTAR.
         * closeOrder() actualmente SOLO calcula totalAmount y persiste.
         * No cambia el campo status. Para que esta transicion sea valida,
         * closeOrder() deberia hacer: this.status = OrderStatus.SERVED
         * (o implementar una maquina de estados explicita).
         */
        @Disabled("closeOrder() no cambia status a SERVED: falta implementar maquina de estados en closeOrder()")
        @Test
        @DisplayName("PENDING -> SERVED via closeOrder(): closeOrder() deberia cambiar el status a SERVED")
        void testTransicion_PendingAServed_ViaCloseOrder_Valida() throws Exception {
            Order order = new Order();
            order.getItems().add(crearItem(1, 10.0));

            try (MockedStatic<XPersistence> xpMock = mockStatic(XPersistence.class)) {
                EntityManager em = mock(EntityManager.class);
                xpMock.when(XPersistence::getManager).thenReturn(em);

                order.closeOrder();

                // FALLA: comportamiento actual devuelve PENDING porque closeOrder() no cambia el status
                assertEquals(OrderStatus.SERVED, order.getStatus());
            }
        }

        /**
         * PENDIENTE DE IMPLEMENTAR.
         * No existe validacion de transiciones. setStatus() acepta SERVED -> SERVED sin restriccion.
         * Para implementar este guard seria necesario sobrescribir setStatus() o usar @PreUpdate.
         */
        @Disabled("No existe validacion de transiciones: setStatus() acepta SERVED->SERVED sin restriccion")
        @Test
        @DisplayName("SERVED -> SERVED: transicion invalida, debe lanzar excepcion o no tener efecto")
        void testTransicion_ServedAServed_Invalida_DebeRechazarse() {
            Order order = new Order();
            order.setStatus(OrderStatus.SERVED);

            // FALLA: no existe guard; setStatus acepta cualquier valor sin validar el estado previo
            assertThrows(IllegalStateException.class, () -> order.setStatus(OrderStatus.SERVED));
        }

        /**
         * PENDIENTE DE IMPLEMENTAR.
         * No existe validacion de transiciones. setStatus() acepta CANCELLED -> CANCELLED sin restriccion.
         */
        @Disabled("No existe validacion de transiciones: setStatus() acepta CANCELLED->CANCELLED sin restriccion")
        @Test
        @DisplayName("CANCELLED -> CANCELLED: transicion invalida, debe lanzar excepcion o no tener efecto")
        void testTransicion_CancelledACancelled_Invalida_DebeRechazarse() {
            Order order = new Order();
            order.setStatus(OrderStatus.CANCELLED);

            // FALLA: no existe guard; setStatus acepta cualquier valor sin validar el estado previo
            assertThrows(IllegalStateException.class, () -> order.setStatus(OrderStatus.CANCELLED));
        }
    }
}
