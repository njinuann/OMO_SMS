/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Pecherk
 */
public abstract class APLog
{
    public abstract void logDebug(Object event);

    public abstract void logEvent(Object event);

    public abstract void logEvent(String input, Object event);

    public abstract void logError(Throwable ex);

    public abstract void setCall(String callRef, Object callObject);

    public abstract void setCall(String callRef, Object callObject, String prefix);

    public abstract void setCall(String callRef, Object callObject, boolean replace);

    public String convertToString(Object object)
    {
        return convertToString(object, null).trim();
    }

    private String convertToString(Object object, String tag)
    {
        tag = decapitalize(tag);
        String text = "", items = "", prevVal = "";
        Class<?> beanClass = !isBlank(object) ? object.getClass() : String.class;
        try
        {
            if (isBlank(object))
            {
                return !isBlank(tag) ? (tag + "=<" + String.valueOf(object) + ">") : String.valueOf(object);
            }
            if (object instanceof JAXBElement)
            {
                return convertToString(extractValue((JAXBElement) object), tag);
            }
            for (MethodDescriptor methodDescriptor : Introspector.getBeanInfo(beanClass).getMethodDescriptors())
            {
                if ("toString".equalsIgnoreCase(methodDescriptor.getName()) && beanClass == methodDescriptor.getMethod().getDeclaringClass() && !(methodDescriptor.getMethod().getAnnotation(AXIgnore.class) instanceof AXIgnore))
                {
                    return !isBlank(tag) ? (tag + "=<" + String.valueOf(object) + ">") : String.valueOf(object);
                }
            }
            if (object instanceof byte[])
            {
                return !isBlank(tag) ? (tag + "=<" + new String((byte[]) object) + ">") : new String((byte[]) object);
            }

            tag = isBlank(tag) ? beanClass.getSimpleName() : tag;

            if (object instanceof Collection)
            {
                for (Object item : ((Collection) object).toArray())
                {
                    items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n" : "") + (prevVal = convertToString(item, null)).trim();
                }
                return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>") : (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
            }
            else if (object instanceof Map)
            {
                for (Object key : ((Map) object).keySet())
                {
                    items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n" : "") + (prevVal = convertToString(((Map) object).get(key), String.valueOf(key))).trim();
                }
                return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>") : (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
            }
            else if (beanClass.isArray())
            {
                switch (beanClass.getSimpleName())
                {
                    case "int[]":
                        return (tag + "=<" + Arrays.toString((int[]) object) + ">");
                    case "long[]":
                        return (tag + "=<" + Arrays.toString((long[]) object) + ">");
                    case "boolean[]":
                        return (tag + "=<" + Arrays.toString((boolean[]) object) + ">");
                    case "byte[]":
                        return (tag + "=<" + Arrays.toString((byte[]) object) + ">");
                    case "char[]":
                        return (tag + "=<" + Arrays.toString((char[]) object) + ">");
                    case "double[]":
                        return (tag + "=<" + Arrays.toString((double[]) object) + ">");
                    case "float[]":
                        return (tag + "=<" + Arrays.toString((float[]) object) + ">");
                    case "short[]":
                        return (tag + "=<" + Arrays.toString((short[]) object) + ">");
                }
                if (object instanceof Object[])
                {
                    for (Object item : (Object[]) object)
                    {
                        items += (isBlank(items) ? "" : ", ") + (prevVal.contains("\r\n") ? "\r\n\t" : "") + (prevVal = convertToString(item, null)).trim();
                    }
                    return items.contains("\r\n") ? ("\r\n" + tag + "=<[" + "\r\n\t" + items + "\r\n" + "]>") : (tag + "=<[" + (!isBlank(items) ? " " + items + " " : "") + "]>");
                }
                else
                {
                    return (tag + "=<[" + String.valueOf(object) + "]>");
                }
            }
            else
            {
                Method readMethod;
                for (PropertyDescriptor propertyDesc : Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
                {
                    if ((readMethod = propertyDesc.getReadMethod()) != null)
                    {
                        Object value = readMethod.invoke(object);
                        if (!(value instanceof Class))
                        {
                            text += (isBlank(text) ? "" : ", ") + convertToString(value, propertyDesc.getName());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logError(ex);
        }
        return (!isBlank(tag) ? (tag + "=<[ " + (isBlank(text) ? String.valueOf(object) : text) + " ]>") : (isBlank(text) ? String.valueOf(object) : text));
    }

    public String capitalize(String text, boolean convertAllXters)
    {
        if (text != null && text.length() > 0)
        {
            StringBuilder builder = new StringBuilder();
            for (String word : text.replace("_", " ").split("\\s"))
            {
                builder.append(word.length() > 2 ? word.substring(0, 1).toUpperCase() + (convertAllXters ? word.substring(1).toLowerCase() : word.substring(1)) : word.toLowerCase()).append(" ");
            }
            return builder.toString().trim();
        }
        return text;
    }

    public String decapitalize(String text)
    {
        if (text != null && text.length() > 0)
        {
            StringBuilder builder = new StringBuilder();
            for (String word : text.split("\\s"))
            {
                builder.append(word.substring(0, 1).toLowerCase()).append(word.substring(1)).append(" ");
            }
            return builder.toString().trim();
        }
        return text;
    }

    public void logDebug(String callRef, Object event, String prefix)
    {
        if (this instanceof AXCaller && APController.isDebugMode())
        {
            setCall(callRef, event, prefix);
        }
        else if (this instanceof AXLogger)
        {
            logDebug(event);
        }
    }

    public void logDebug(String callRef, Object event)
    {
        if (this instanceof AXCaller && APController.isDebugMode())
        {
            setCall(callRef, event);
        }
        else if (this instanceof AXLogger)
        {
            logDebug(event);
        }
    }

    public <T> T extractValue(JAXBElement<T> element)
    {
        return !isBlank(element) ? (T) element.getValue() : null;
    }

    public boolean isBlank(Object object)
    {
        return object == null || "".equals(String.valueOf(object).trim()) || "null".equals(String.valueOf(object).trim());
    }

    public String yesNo(boolean isYes)
    {
        return isYes ? "Yes" : "No";
    }

    public String checkBlank(String value)
    {
        return checkBlank(value, "");
    }

    public <T> T checkBlank(T value, T nillValue)
    {
        return !isBlank(value) ? value : nillValue;
    }

    public String capitalize(String text)
    {
        return capitalize(text, true);
    }
}
