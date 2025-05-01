import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PreparacionPedidos2 extends GestorDePedidos implements Runnable {
    private static int Contador = 0;
    private final GestorDePedidos pedidos; //TENEMOS QUE VER COMO LO IMPLEMENTAN EN LA CLASE PRINCIPAL
    private final List<RegistroDePedido> PedidosEnPreparacion; //TENDRIAMOS QUE VER COMO LO HACEN EN LA CLASE REGISTRODEPEDIDO
    private Random random = new Random();
    private final int maxPedidos;
    private final int tiempoMin;
    private final int tiempoMax;
    public PreparacionPedidos2(GestorDePedidos almacen, List<RegistroDePedido> pedidosEnPreparacion, int maxPedidos, int tiempoMin, int tiempoMax){
        this.almacen = almacen;
        this.PedidosEnPreparacion = pedidosEnPreparacion;
        this.maxPedidos = maxPedidos;
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        List<RegistroDePedidos> PedidosEnPreparacion = new ArrayList<RegistroDePedidos>();
    }

    @Override
    public void run() {
        while (true) { //Ejecuto mientras que no se llegue al maximo de pedidos
            try {
                int id;
                synchronized (PreparacionPedidos.class){ //protejo para evitar concurrencia de dos hilos
                    if(Contador >= maxPedidos)break; //si el contador de pedidos supera o es igual al meximo de pedidor freno la sentencia
                    id=++Contador;                   //sino incremeento el contador en 1
                }
                Casillero casillero = almacen.getCasilleroLibre();//Creamos un objeto tipo Casillero (Vemos como se llama cuando tengamos la clase gestor de pedidos)
                if (casillero != null) { //pregunto si esta vacio
                    Thread.sleep(50);   //si no esta vacio espero 50 milisegundos hasta buscas de nuevo
                    continue;

                }
                RegistroDePedido registro = new RegistroDePedido(id, casillero); //creo un nuevo registro (vemos como se llama cuando tengamos la clase)
                synchronized (pedidosEnPreparacion) { //protejer a pedidosEnPreparacion para que no trabajen los dos hilos
                    PedidosEnPreparacion.add(registro); //Agrego el pedido a la lista
                }
                DormirProceso(); //Simulo el tiempo de procesamiento de predido, podemos sacarlo si no llega a hacer falta
            } catch (InterruptedException e) { //Que pasa si el hilo es interrumpido, lanzo excepcion
                Thread.currentThread().interrupt(); //Si se da la excepcion salgo del bucle
                break;
            }
        }
    }
    private void DormirProceso() throws InterruptedException{
        int demora = ThreadLocalRandom.current().nextInt(tiempoMin,tiempoMax+1); //Aca lo que hacemos es generar un numero random entre una cota inferior
        Thread.sleep(demora);                                                          //y una superior como nos pide el problema

    }

}

