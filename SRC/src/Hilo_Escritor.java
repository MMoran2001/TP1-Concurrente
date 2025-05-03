import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hilo_Escritor extends Thread {
    private final File archivoLog;
    private volatile boolean activo = true; // para detener el hilo más adelante si querés
    private final Gestor gestor;
    private long tiempoFinal = -1;

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
                if (tiempoFinal >= 0) {
                    writer.write("\nFIN DE EJECUCIÓN \n");
                    writer.write("Tiempo total de ejecución: " + tiempoFinal + " ms\n");
                    writer.flush();
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error en el LoggerHilo: " + e.getMessage());
            }
    }
    public void detener() {
        activo = false;
        this.interrupt(); // por si está dormido
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





}

//Calcular el tiempo total de el programa, calcular estadisiticas de casilleros.