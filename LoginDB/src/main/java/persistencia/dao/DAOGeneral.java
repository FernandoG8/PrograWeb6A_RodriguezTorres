package persistencia.dao;

import java.util.ArrayList;

public interface DAOGeneral<K, E> {
    public boolean agregar(E elemento);

    public ArrayList<E> consultar();

    public boolean actualizar(K id, E nuevo);

    public boolean eliminar(K id);
}
