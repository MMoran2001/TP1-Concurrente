public class Pedido{
    private final Estado_Pedidos tipoPedido;

    public Pedido(Estado_Pedidos tipoPedido){
        this.tipoPedido = tipoPedido;
    }
    public Estado_Pedidos getTipoPedido(){
        return tipoPedido;
    }
}
