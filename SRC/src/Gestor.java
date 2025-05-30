

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class Gestor {

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
        almacen = new Casillero[10][20];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
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
            if ((almacen[i][j].getEstado() == Estado_Casilleros.VACIO)&& (getPreparados()<500)){
                almacen[i][j].cambiarEstado(Estado_Casilleros.OCUPADO);
                almacen[i][j].aumentarContador();
                pedEnPrep.agregarPedido(new Pedido(Estado_Pedidos.EN_PREPARACION));
                System.out.println("Contador de pedidos: " + pedEnPrep.getContador() + "Contador total de pedidos:" + getPreparados());
                return true;
            }
        }

        return false;
    }

    public void modificarRegistro(Registro registro, String operacion) {
        switch (operacion) {
            case "AGREGAR":
                registro.agregarPedido(new Pedido(registro.getTipoPedido()));
                break;
            case "ELIMINAR":
                registro.eliminarPedido();
                break;
            default:
                System.out.println("Operacion no valida");
                break;

        }
    }

    public synchronized int addDespachados() {

        return pedidosDespachados.incrementAndGet();

    }

    public synchronized int getDespachados() {
        return pedidosDespachados.get();
    }


    public synchronized void addPreparados() {

        pedidosPreparados.incrementAndGet();

    }

    public synchronized int getPreparados() {
        return pedidosPreparados.get();
    }

    public synchronized int addEntregados() {

        return pedidosEntregados.incrementAndGet();

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
        int i = random.nextInt(10);
        int j = random.nextInt(20);
        return new int[]{i, j};
    }

    public Casillero[][] getAlmacen() {
        return almacen;
    }

    public Registro getPedEnTran() {
        return pedEnTran;
    }

    public Registro getPedEnPrep() {
        return pedEnPrep;
    }

    public Registro getPedFallido() {
        return pedFallido;
    }

    public Registro getPedEntregado() {
        return pedEntregado;
    }

    public Registro getPedVerificado() {
        return pedVerificado;
    }

    public Object getMonitorEntrega() {
        return monitorEntrega;
    }

    public Object getMonitorDespacho() {
        return monitorDespacho;
    }

    public Object getMonitorVerificacion() {
        return monitorVerificacion;
    }

    public boolean isPreparacionDone() {
        return preparacionDone;
    }

    public boolean isDespachoDone() {

        return despachoDone;
    }

    public boolean isEntregaDone() {
        return entregaDone;
    }

    public boolean isVerificacionDone() {
        return verificacionDone;
    }

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






