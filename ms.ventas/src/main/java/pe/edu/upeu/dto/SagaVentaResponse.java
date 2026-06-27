package pe.edu.upeu.dto;

import pe.edu.upeu.entity.Venta;

public class SagaVentaResponse {

    public String sagaId;
    public String estado;
    public String mensaje;
    public Venta venta;

    public SagaVentaResponse() {
    }

    public SagaVentaResponse(String sagaId, String estado, String mensaje, Venta venta) {
        this.sagaId = sagaId;
        this.estado = estado;
        this.mensaje = mensaje;
        this.venta = venta;
    }
}
