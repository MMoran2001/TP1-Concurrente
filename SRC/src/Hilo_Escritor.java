import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hilo_Escritor extends Thread {
    private final File archivoLog;
    private volatile boolean activo = true; // para detener el hilo más adelante si querés
    private final Gestor gestor;

    public Hilo_Escritor() {
        gestor = Gestor.getMiGestor();
        String userHome = System.getProperty("user.home");
        String documentosPath = userHome + File.separator + "Documents"; // o "Documentos"
        File carpeta = new File(documentosPath);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        archivoLog = new File(carpeta, "log.txt");
    }

    @Override
    public void run() {
            try (FileWriter writer = new FileWriter(archivoLog, true)) {
                while (activo) {
                    String Resultados = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                    writer.write("Cantidad de pedidos verificados: " + gestor.getPedVerificado().getContador() + "\n"
                            + "Cantidad de pedidos fallidos: " + gestor.getPedFallido().getContador() + "\n");
                    System.out.println("Archivo creado en: " + archivoLog.getAbsolutePath());
                    Thread.sleep(200);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error en el LoggerHilo: " + e.getMessage());
            }
    }
    public void detener() {
        activo = false;
        this.interrupt(); // por si está dormido
    }
}

