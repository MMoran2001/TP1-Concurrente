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

}


public boolean TomarPedido(int i, int j){

        synchronized (almacen[i][j]) {
            if (almacen[i][j].getEstado() == Estado_Casilleros.VACIO) {
                almacen[i][j].cambiarEstado(Estado_Casilleros.OCUPADO);
                almacen[j][j].aumentarContador();
                pedEnPrep.agregarPedido();
                return true;
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



