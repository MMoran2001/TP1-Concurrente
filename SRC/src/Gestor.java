import java.util.Random;

public final class Gestor{

    private static Gestor miGestor;
    private static Casillero[][] almacen;
    private static Registro pedEnPrep;
    private static Registro pedEnTran;
    private static Registro pedEntregado;
    private static Registro pedVerificado;
    private static Registro pedFallido;


public static synchronized Gestor getMiGestor(){
    if(miGestor == null){
        miGestor = new Gestor();
    }
    return miGestor;
}

private Gestor(){
    almacen = new Casillero[10][20];
    pedEnPrep = new Registro(Estado_Pedidos.EN_PREPARACION);
    pedEnTran = new Registro(Estado_Pedidos.EN_TRANSITO);
    pedEntregado = new Registro(Estado_Pedidos.ENTREGADO);
    pedVerificado = new Registro(Estado_Pedidos.VERIFICADO);
    pedFallido = new Registro(Estado_Pedidos.FALLIDO);

}

private static int[] randomPos(){
        Random random = new Random();
        int i = random.nextInt(10);
        int j = random.nextInt(20);
        return new int[]{i, j};
}



public static void TomarPedido(){
    boolean pedidoTomado = false;
    while(!pedidoTomado){
        int[] pos = randomPos();
        synchronized (almacen[pos[0]][pos[1]]) {
            if (almacen[pos[0]][pos[1]].getEstado() == Estado_Casilleros.VACIO) {
                almacen[pos[0]][pos[1]].cambiarEstado(Estado_Casilleros.OCUPADO);
                almacen[pos[0]][pos[1]].aumentarContador();
                pedEnPrep.agregarPedido();
                pedidoTomado = true;
            }
        }
    }
}



public static void modificarRegistro(Registro registro, String operacion){
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


}



