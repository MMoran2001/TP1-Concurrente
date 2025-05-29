public class Registro {
    private int contador;
    private final Estado_Pedidos tipoPedido;


    public Registro(Estado_Pedidos pedido){
        contador = 0;
        tipoPedido = pedido;
    }

    public synchronized void agregarPedido(){
        contador++;
    }

    public synchronized void eliminarPedido(){
        contador--;
    }


    public synchronized int getContador(){
        return contador;
    }

    public Estado_Pedidos getTipoPedido(){
        return tipoPedido;
    }


}
