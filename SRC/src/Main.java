public class Main {
    public static void main(String[] args) {
        Gestor gestor = Gestor.getMiGestor();


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