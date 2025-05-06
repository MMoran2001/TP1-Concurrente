public class Registro {
    private int contador;
    private final Estado_Pedidos tipoPedido;


    public Registro(Estado_Pedidos pedido){
        contador = 0;
        tipoPedido = pedido;
    }

    public void agregarPedido(){
        contador++;
    }

    public void eliminarPedido(){
        contador--;
    }


    public int getContador(){
        return contador;
    }

    public Estado_Pedidos getTipoPedido(){
        return tipoPedido;
    }


}
