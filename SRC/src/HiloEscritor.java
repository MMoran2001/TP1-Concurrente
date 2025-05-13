import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiloEscritor extends Thread {
    private final File archivoLog;
    private volatile boolean activo = true; // para detener el hilo más adelante si querés
    private final Gestor gestor;
    private long tiempoFinal = -1;

    public HiloEscritor() {
        gestor = Gestor.getMiGestor();
        //String userHome = System.getProperty("user.home");
       // String documentosPath = userHome + File.separator + "Documents"; // o "Documentos"
       // File carpeta = new File(documentosPath);
        File carpeta = new File("logs");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        archivoLog = new File(carpeta, "Registro de ejecucion.txt");
    }

    @Override
    public void run() {
        while (activo){
            escribirLog();
        }
        escribirLog();
        System.out.println("Fin del hilo de escritura");
    }

    public void detener() {
        activo = false;// por si está dormido
    }

    public void registrarTiempoEjecucion(long tiempo) {
        this.tiempoFinal = tiempo;
    }

    public void registrarCasilleroMasUsado(String info) {
        try (FileWriter writer = new FileWriter(archivoLog, true)) {
            writer.write(info + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void escribirLog(){
        try (FileWriter writer = new FileWriter(archivoLog)) {


                writer.write( "Cantidad de pedidos verificados: " + gestor.getPedVerificado().getContador() + "\n"
                        + "Cantidad de pedidos fallidos: " + gestor.getPedFallido().getContador() + "\n");
                System.out.println("Archivo creado en: " + archivoLog.getAbsolutePath());
                Thread.sleep(200);

            if (tiempoFinal != -1) {
                writer.write("\nFIN DE EJECUCIÓN \n");
                writer.write("Tiempo total de ejecución: " + tiempoFinal + " ms\n");
                writer.flush();
            }
            registrarCasilleroMasUsado(gestor.getCasilleroMasUsado());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error en el LoggerHilo: " + e.getMessage());
        }
    }

}

