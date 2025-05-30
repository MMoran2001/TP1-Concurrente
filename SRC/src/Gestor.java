import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class Gestor {
    private static final int MAX_PEDIDOS = 500;
    private static final int FILAS_ALMACEN = 10;
    private static final int COLUMNAS_ALMACEN = 20;

    private static Gestor miGestor;
    private static Casillero[][] almacen;
    private static Registro pedEnPrep;
    private static Registro pedEnTran;
    private static Registro pedEntregado;
    private static Registro pedVerificado;
    private static Registro pedFallido;
    private final AtomicInteger pedidosDespachados = new AtomicInteger(0);
    private final AtomicInteger pedidosPreparados = new AtomicInteger(0);
    private final AtomicInteger pedidosEntregados = new AtomicInteger(0);
    private final Object monitorEntrega;
    private final Object monitorDespacho;
    private final Object monitorVerificacion;
    private boolean preparacionDone;
    private boolean despachoDone;
    private boolean entregaDone;
    private boolean verificacionDone;

    public static synchronized Gestor getMiGestor() {
        if (miGestor == null) {
            miGestor = new Gestor();
        }
        return miGestor;
    }

    private Gestor() {
        almacen = new Casillero[FILAS_ALMACEN][COLUMNAS_ALMACEN];
        for (int i = 0; i < FILAS_ALMACEN; i++) {
            for (int j = 0; j < COLUMNAS_ALMACEN; j++) {
                almacen[i][j] = new Casillero();
            }
        }
        pedEnPrep = new Registro(Estado_Pedidos.EN_PREPARACION);
        pedEnTran = new Registro(Estado_Pedidos.EN_TRANSITO);
        pedEntregado = new Registro(Estado_Pedidos.ENTREGADO);
        pedVerificado = new Registro(Estado_Pedidos.VERIFICADO);
        pedFallido = new Registro(Estado_Pedidos.FALLIDO);

        monitorEntrega = new Object();
        monitorVerificacion = new Object();
        monitorDespacho = new Object();

        preparacionDone = false;
        despachoDone = false;
        entregaDone = false;
        verificacionDone = false;
    }

    public boolean TomarPedido(int i, int j) {
        synchronized (almacen[i][j]) {
            if (almacen[i][j].ocuparSiVacio()) {
                pedEnPrep.agregarPedido();
                return true;
            }
        }
        return false;
    }

    public void modificarRegistro(Registro registro, String operacion) {
        switch (operacion) {
            case "AGREGAR":
                registro.agregarPedido();
                break;
            case "ELIMINAR":
                registro.eliminarPedido();
                break;
            default:
                System.out.println("Operacion no valida");
                break;
        }
    }

    private synchronized boolean incrementarContadorSiPosible(AtomicInteger contador) {
        if (contador.get() < MAX_PEDIDOS) {
            contador.incrementAndGet();
            return true;
        }
        return false;
    }

    public synchronized int procesarPedido(String tipo) {
        switch (tipo) {
            case "PREPARADO":
                return incrementarContadorSiPosible(pedidosPreparados) ? pedidosPreparados.get() : -1;
            case "DESPACHADO":
                return incrementarContadorSiPosible(pedidosDespachados) ? pedidosDespachados.get() : -1;
            case "ENTREGADO":
                return incrementarContadorSiPosible(pedidosEntregados) ? pedidosEntregados.get() : -1;
            default:
                return -1;
        }
    }

    public synchronized int getDespachados() {
        return pedidosDespachados.get();
    }

    public synchronized int getPreparados() {
        return pedidosPreparados.get();
    }

    public synchronized int getEntregados() {
        return pedidosEntregados.get();
    }

    public void markPreparacionDone() {
        synchronized (monitorDespacho) {
            preparacionDone = true;
            monitorDespacho.notifyAll();
        }
    }

    public void markDespachoDone() {
        synchronized (monitorEntrega) {
            despachoDone = true;
            monitorEntrega.notifyAll();
        }
    }

    public void markEntregaDone() {
        synchronized (monitorVerificacion) {
            entregaDone = true;
            monitorVerificacion.notifyAll();
        }
    }

    public void markVerificacionDone() {
        verificacionDone = true;
    }

    public int[] randomPos() {
        Random random = new Random();
        int i = random.nextInt(FILAS_ALMACEN);
        int j = random.nextInt(COLUMNAS_ALMACEN);
        return new int[]{i, j};
    }

    // Getters
    public Casillero[][] getAlmacen() { return almacen; }
    public Registro getPedEnTran() { return pedEnTran; }
    public Registro getPedEnPrep() { return pedEnPrep; }
    public Registro getPedFallido() { return pedFallido; }
    public Registro getPedEntregado() { return pedEntregado; }
    public Registro getPedVerificado() { return pedVerificado; }
    public Object getMonitorEntrega() { return monitorEntrega; }
    public Object getMonitorDespacho() { return monitorDespacho; }
    public Object getMonitorVerificacion() { return monitorVerificacion; }
    public boolean isPreparacionDone() { return preparacionDone; }
    public boolean isDespachoDone() { return despachoDone; }
    public boolean isEntregaDone() { return entregaDone; }
    public boolean isVerificacionDone() { return verificacionDone; }

    public String getCasilleroMasUsado() {
        int maxUso = -1;
        int fila = -1;
        int columna = -1;

        for (int i = 0; i < almacen.length; i++) {
            for (int j = 0; j < almacen[i].length; j++) {
                int usos = almacen[i][j].getContador();
                if (usos > maxUso) {
                    maxUso = usos;
                    fila = i;
                    columna = j;
                }
            }
        }

        return "\nCasillero más usado: [" + fila + "][" + columna + "] con " + maxUso + " usos.";
    }
}









