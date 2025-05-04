public class Main {
    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        Gestor gestor = Gestor.getMiGestor();
        int tiempoMin = 30;
        int tiempoMax = 90;
        Hilo_Escritor hiloLogger = new Hilo_Escritor();
        hiloLogger.start();

        for (int i = 0; i < 3; i++) {
            Preparacion preparacion = new Preparacion(tiempoMin, tiempoMax);
            Thread hiloPreparacion = new Thread(preparacion);
            hiloPreparacion.start();
        }
        for (int i = 0; i < 2; i++) {
            Despacho despacho = new Despacho(tiempoMin, tiempoMax);
            Thread hiloDespacho = new Thread(despacho);
            hiloDespacho.start();
        }
        for (int i = 0; i < 3; i++) {
            Entrega entrega = new Entrega(tiempoMin, tiempoMax);
            Thread hiloEntrega = new Thread(entrega);
            hiloEntrega.start();
        }

        for (int i = 0; i < 2; i++) {
            Verificacion verificacion = new Verificacion(tiempoMin, tiempoMax);
            Thread hiloVerificacion = new Thread(verificacion);
            hiloVerificacion.start();
        }


        while (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador() < 500) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println("Me interrumpieron!");
            }
        }

        long fin = System.currentTimeMillis();
        long duracion = fin - inicio;
        hiloLogger.registrarTiempoEjecucion(duracion);
        hiloLogger.registrarCasilleroMasUsado(gestor.getCasilleroMasUsado());
        hiloLogger.detener();

        System.out.println("Fin del programa");
    }

}



////Creo el file para almacenar el log
//
//String userDirectory = System.getProperty("user.home");
//String filePath = userDirectory + File.separator + "log.txt";
//
//
////Creo un log para escribir los estados de los hilos en la ejecucion
//        try (FileWriter file = new FileWriter(filePath);
//PrintWriter pw = new PrintWriter(file)) {
//
//
//        //Escribo el estado de cada hilo
//        for(int i = 0; i < 10; i++){
//        pw.println("Main : Status of Thread " + i + " : " + threads[i].getState());
//status[i] = threads[i].getState();
//        }
//
//
//private static void writeThreadInfo(PrintWriter pw, Thread thread, Thread.State state){
//    pw.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
//    pw.printf("Main : Priority: %d\n", thread.getPriority());
//    pw.printf("Main : Old State: %s\n", state);
//    pw.printf("Main : New State %s\n", thread.getState());
//    pw.printf("****************************************\n");