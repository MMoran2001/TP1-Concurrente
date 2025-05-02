import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public final class Gestor{

    private static Gestor miGestor;
    private static Casillero[][] almacen;
    private static Registro pedEnPrep;
    private static Registro pedEnTran;
    private static Registro pedEntregado;
    private static Registro pedVerificado;
    private static Registro pedFallido;
    private final AtomicInteger contador;
    private final AtomicInteger pedidosDespachados= new AtomicInteger(0);
    private final AtomicInteger pedidosPreparados= new AtomicInteger(0);
    private final AtomicInteger pedidosEntregados= new AtomicInteger(0);

public static synchronized Gestor getMiGestor(){
    if(miGestor == null){
        miGestor = new Gestor();
    }
    return miGestor;
}

private Gestor(){
    almacen = new Casillero[10][20];
    for(int i = 0; i < 10; i++){
        for(int j = 0; j < 20; j++){
            almacen[i][j] = new Casillero();
        }
    }
    pedEnPrep = new Registro(Estado_Pedidos.EN_PREPARACION);
    pedEnTran = new Registro(Estado_Pedidos.EN_TRANSITO);
    pedEntregado = new Registro(Estado_Pedidos.ENTREGADO);
    pedVerificado = new Registro(Estado_Pedidos.VERIFICADO);
    pedFallido = new Registro(Estado_Pedidos.FALLIDO);

    contador = new AtomicInteger(0);
}

public boolean TomarPedido(int i, int j){

        synchronized (almacen[i][j]) {
            if (almacen[i][j].getEstado() == Estado_Casilleros.VACIO) {
                almacen[i][j].cambiarEstado(Estado_Casilleros.OCUPADO);
                almacen[i][j].aumentarContador();
                pedEnPrep.agregarPedido();
                return true;
            }
        }

    return false;
}

public void modificarRegistro(Registro registro, String operacion){
    switch (operacion){
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

public int addDespachado(){

    return pedidosDespachados.incrementAndGet();

}
public int addPreparados(){

    return pedidosPreparados.incrementAndGet();

}
public int addEntregados(){

    return pedidosEntregados.incrementAndGet();

}

public int[] randomPos(){
        Random random = new Random();
        int i = random.nextInt(10);
        int j = random.nextInt(20);
        return new int[]{i, j};
}

public Casillero[][] getAlmacen() {
    return almacen;
}

public int getContador(){
    return contador.get();
}

public void aumentarContador(){
    contador.incrementAndGet();
}

public  Registro getPedEnTran(){
    return pedEnTran;
}

public  Registro getPedEnPrep(){
    return pedEnPrep;
}

public  Registro getPedFallido(){
    return pedFallido;
}

public  Registro getPedEntregado(){
    return pedEntregado;
}

public  Registro getPedVerificado(){
    return pedVerificado;
}


}



