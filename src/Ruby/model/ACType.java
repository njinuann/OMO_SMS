/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

/**
 *
 * @author Pecherk
 */
public enum ACType
{
    SAVING
    {
        @Override
        public String toString()
        {
            return "Saving";
        }
    },
    LOAN
    {
        @Override
        public String toString()
        {
            return "Loan";
        }
    },
    ALL
    {
        @Override
        public String toString()
        {
            return "All";
        }
    };
}
