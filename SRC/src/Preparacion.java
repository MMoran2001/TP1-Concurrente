import java.util.concurrent.ThreadLocalRandom;


public class Preparacion extends Thread {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;

    public Preparacion(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;

    }

    @Override
    public void run() {
        while (true) {        //Ejecuto mientras que no se llegue al maximo de pedidos
            try {
                if (gestor.getPreparados() >= 500) {
                    synchronized (gestor.getMonitorDespacho()){
                        gestor.markPreparacionDone();
                        gestor.getMonitorDespacho().notifyAll();
                    }
                    System.out.println("FIN DE PREPARACION");
                    break; //Una vez que se prepararon 500 pedidos termina el hilo
                }
                PrepararPedido(); // hace lo necesario para preparar
                DormirProceso();  // simula la demora
                gestor.addPreparados();
                synchronized (gestor.getMonitorDespacho()){
                    gestor.getMonitorDespacho().notifyAll();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();                                     //Si se da la excepcion salgo del bucle
                break;//Simulo el tiempo de procesamiento de pedido, podemos sacarlo si no llega a hacer falta
            }
            //Que pasa si el hilo es interrumpido, lanzo excepcion
        }

    }

    private void DormirProceso() throws InterruptedException{
        int demora = ThreadLocalRandom.current().nextInt(tiempoMin,tiempoMax+1); //Aca lo que hacemos es generar un numero random entre una cota inferior
        Thread.sleep(demora);                                                          //y una superior como nos pide el problema
    }

    public void PrepararPedido() {
                                                                //si el contador de pedidos supera o es igual al meximo de pedidor freno la sentencia

        boolean pedidoTomado = false;
        while (!pedidoTomado) {
            int[] pos = gestor.randomPos();
            pedidoTomado = gestor.TomarPedido(pos[0], pos[1]);
            if (pedidoTomado) {
                try {
                    DormirProceso();
                } catch (InterruptedException e) {
                    System.out.println("Me interrumpieron!");
                }
            }
        }
    }
}

// Registro registro = new Registro(Estado_Pedidos.EN_PREPARACION); //creo un nuevo registro (vemos como se llama cuando tengamos la clase)
//                synchronized (pedidosEnPreparacion) { //protejer a pedidosEnPreparacion para que no trabajen los dos hilos
//                    PedidosEnPreparacion.add(registro); //Agrego el pedido a la lista
//                }

// if (Casillero != null) { //pregunto si esta vacio
//                    Thread.sleep(50);   //si no esta vacio espero 50 milisegundos hasta buscas de nuevo
//                    continue;
//
//                }
//    private static int[] randomPos(){
//        Random random = new Random();
//        int i = random.nextInt(10);
//        int j = random.nextInt(20);
//        return new int[]{i, j};
//    }