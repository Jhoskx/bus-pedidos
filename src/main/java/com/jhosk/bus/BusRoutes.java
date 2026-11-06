package com.jhosk.bus;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class BusRoutes extends RouteBuilder {
@Override

    public void configure() throws Exception {
        
        // Manejo de errores del BUS: si algo falla, se reintenta y,
        // si sigue fallando, el pedido se envía al DLC (buzón de devoluciones)
        errorHandler(deadLetterChannel("direct:pedidos-fallidos")
            // Número de reintentos después del primer intento fallido (4 intentos en total)
            .maximumRedeliveries(3)
            // Tiempo de espera entre reintentos, en milisegundos (1000 = 1 segundo)
            .redeliveryDelay(1000)
            // Registra cada reintento en el log como advertencia (WARN)
            .retryAttemptedLogLevel(LoggingLevel.WARN));



        rest("/pedidos")                         
            .post()                               
            .to("direct:recibir-pedido");


          from("direct:recibir-pedido")
            .routeId("recibir-pedido")
                        .choice()
                .when(header("CorrelationID").isNull())
                    .log("Pedido RECHAZADO: falta el header CorrelationID")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                    .setHeader("Content-Type", constant("application/json"))
                    .setBody(constant("{\"error\": \"Falta el header CorrelationID\"}"))
                    .stop()
            .end()
            .log("Pedido recibido  | CorrelationID=${header.CorrelationID} | TraceID = ${header.TraceID}")
            
            .log("Contenido: ${body}")
            .wireTap("direct:auditoria")
            .to("jslt:jslt/pedido-a-core.jslt")
            .log("pedido traducido para el core : ${body}")
            .circuitBreaker()
            .faultToleranceConfiguration()
            .requestVolumeThreshold(4)                    // Evalúa los últimos 4 pedidos
            .failureRatio(50)                                     // Si falla el 50 % o más, el breaker se abre
            .delay(10000)                      // Se queda abierto 10 segundos antes de volver a probar
            .end()
            .to("direct:core-legacy")                // La llamada protegida al Core Legacy
            .onFallback()

            .log(LoggingLevel.WARN, "Circuit Breaker: Core no disponible, respondiendo con plan B para ${header.CorrelationID}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                .setHeader("Content-Type", constant("application/json"))
                .setBody(simple("{\"error\": \"Core Legacy no disponible, intente más tarde\", \"correlationId\": \"${header.CorrelationID}\"}"))
            .end();




            
                    // DLC: buzón de devoluciones. Aquí llegan los pedidos que
        // fallaron incluso después de todos los reintentos
        from("direct:pedidos-fallidos")
            .routeId("pedidos-fallidos")
            .log(LoggingLevel.ERROR, "DLC: el pedido ${header.CorrelationID} no se pudo entregar. Motivo: ${exception.message}")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
            .setHeader("Content-Type", constant("application/json"))
            .setBody(simple("{\"error\": \"Pedido no procesado, quedó registrado para revisión\", \"correlationId\": \"${header.CorrelationID}\"}"));
    }
}
