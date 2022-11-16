/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.util.function.BiConsumer;

/**
 *
 * @author Pecherk
 * @param <T>
 * @param <U>
 * @param <V>
 */
public class AXFunction<T, U, V>
{
    U param1;
    V param2;
    BiConsumer function;

    public AXFunction(BiConsumer<U, V> function, U param1, V param2)
    {
        this.function = function;
        this.param1 = param1;
        this.param2 = param2;
    }

    public void execute()
    {
        function.accept(param1, param2);
    }
}
