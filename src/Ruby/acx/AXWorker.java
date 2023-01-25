/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import Ruby.DBClient;
import static Ruby.acx.AXConstant.HUNDRED;
import Ruby.model.AXCharge;
import Ruby.model.AXSetting;
import Ruby.model.CNAccount;
import Ruby.model.CNBranch;
import Ruby.model.AXPage;
import Ruby.model.AXRequest;
import Ruby.model.AXTier;
import Ruby.model.CNActivity;
import Ruby.model.CNCurrency;
import Ruby.model.TCDeduction;
import Ruby.model.TCSplit;
import Ruby.model.TCValue;
import Ruby.model.TCWaiver;
import com.neptunesoftware.supernova.ws.common.XAPIErrorData;
import com.neptunesoftware.supernova.ws.common.XAPIException;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 *
 * @author Pecherk
 */
public final class AXWorker implements Serializable
{

    private ATBox box;
    private final Pattern holderPattern = Pattern.compile("\\{.*?\\}");
    private final Pattern namePattern = Pattern.compile("^[a-zA-Z\\s]*$");

    public final DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    private final HashMap<String, String> escapeXters = new HashMap<>();
    private final HashMap<String, String> unescapeXters = new HashMap<>();
    private final Pattern rTrimPattern = Pattern.compile("\\s+$");
    private final Pattern lTrimPattern = Pattern.compile("^\\s+");

    public AXWorker(ATBox box)
    {
        setBox(box);
        initialize();
    }

    private void initialize()
    {
        escapeXters.put("&", "&amp;");
        unescapeXters.put("&lt;", "<");
        unescapeXters.put("&gt;", ">");
        unescapeXters.put("&amp;", "&");
        unescapeXters.put("&apos;", "'");
        unescapeXters.put("&quot;", "\"");
    }

    public <T> T getSetting(AXProperties settings, String code, Class<T> clazz)
    {
        if (settings.containsKey(code))
        {
            try
            {
                String value = settings.getProperty(code);
                return clazz != ArrayList.class ? (T) clazz.getConstructor(String.class).newInstance(value) : createList(value, clazz);
            }
            catch (Exception ex)
            {
                getLog().logEvent(code, ex);
            }
        }
        return null;
    }

    public <T> T getSetting(TreeMap<String, AXSetting> settings, String code, Class<T> clazz, T defaultValue)
    {
        if (settings.containsKey(code))
        {
            try
            {
                String value = settings.get(code).getValue();
                if (clazz == ArrayList.class)
                {
                    return createList(value, clazz);
                }
                else if (clazz == CNAccount.class)
                {
                    return (T) getdClient().queryAnyAccount(value);
                }
                else if (clazz == CNBranch.class)
                {
                    return (T) getdClient().queryBusinessUnit(Long.valueOf(value));
                }
                else
                {
                    return (T) clazz.getConstructor(String.class).newInstance(value);
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(code, ex);
            }
        }
        return defaultValue;
    }

    public TreeMap<String, AXSetting> configure(Class<?> clazz, String channel)
    {
        TreeMap<String, AXSetting> settings = getdClient().querySettings(channel);
        try
        {
            for (Field field : clazz.getDeclaredFields())
            {
                if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()))
                {
                    try
                    {
                        if (isSettings(field))
                        {
                            field.set(null, settings);
                        }
                        else if (!settings.containsKey(field.getName()))
                        {
                            getdClient().saveSetting(createSetting(field, channel));
                        }
                        else
                        {
                            field.set(null, getSetting(settings, field.getName(), field.getType(), null));
                        }
                    }
                    catch (Exception ex)
                    {
                        getLog().logEvent(field.getName(), ex);
                    }
                    getLog().logDebug("Setting=<" + field.getName() + "=" + convertToString(field.get(null)) + ">");
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }

        return settings;
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
            getLog().logEvent(ex);
        }
        return (!isBlank(tag) ? (tag + "=<[ " + (isBlank(text) ? String.valueOf(object) : text) + " ]>") : (isBlank(text) ? String.valueOf(object) : text));
    }

    public JTable createItemTable(LinkedHashMap<String, Object> fieldMap)
    {
        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(
                new Object[][]
                {
                },
                new String[]
                {
                    "Field", "Content", "Description"
                }
        )
        {
            Class[] types = new Class[]
            {
                String.class, String.class, String.class
            };

            @Override
            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return false;
            }
        };
        try
        {
            fieldMap.keySet().stream().map((key)
                    ->
            {
                ArrayList arrayList = new ArrayList();
                arrayList.add(key);
                arrayList.add(fieldMap.get(key));
                arrayList.add(APController.getFields().getProperty(key, capitalize(spaceWords(key))));
                return arrayList;
            }).forEach((arrayList)
                    ->
            {
                tableModel.addRow(arrayList.toArray());
            });

            JTable table = new JTable(tableModel);
            table.getColumnModel().getColumn(0).setMinWidth(150);
            table.getColumnModel().getColumn(1).setMinWidth(300);
            table.getColumnModel().getColumn(2).setMinWidth(300);
            return table;
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return new JTable(tableModel);
    }

    public void extractFields(Object object, LinkedHashMap<String, Object> fieldMap)
    {
        try
        {
            for (PropertyDescriptor propertyDesc : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors())
            {
                Method readMethod = propertyDesc.getReadMethod();
                if (readMethod != null && !Modifier.isNative(readMethod.getModifiers()))
                {
                    fieldMap.put(capitalize(propertyDesc.getName(), false), checkBlank(convertToString(readMethod.invoke(object)), ""));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    public void setCharge(AXRequest request, HashMap<String, String> splitAccounts)
    {
        if (!isBlank(request.getTxnCode()))
        {
            AXCharge aXCharge = getdClient().queryCharge(request.getCharge().getScheme(), request.getTxnCode());
            if (aXCharge != null)
            {
                request.getCharge().setChargeNarration((request.isReversal() ? "REV~" : "") + aXCharge.getDescription());
                request.getCharge().setChargeAccount("R".equals(aXCharge.getChargeAccount()) ? request.getContra() : request.getAccount());

                request.getCharge().setChargeLedger(getdClient().unmaskLedger(aXCharge.getChargeLedger(), request.getBranch()).getAccountNumber());
                request.getCharge().setChannelLedger(getdClient().queryChannelLedger(request.getChannelId(), request.getBranch()).getAccountNumber());

                final String currency = request.getCharge().getChargeAccount().getCurrency().getCurrencyCode();
                TCValue value = aXCharge.getValues().getOrDefault(currency, new TCValue());

                switch (value.getChargeType())
                {
                    case "C":
                        request.getCharge().setChargeAmount(value.getValue());
                        break;
                    case "P":
                        final BigDecimal chargeAmount = request.getAmount().multiply(value.getValue()).divide(HUNDRED, 2, RoundingMode.HALF_UP);
                        request.getCharge().setChargeAmount(BigDecimal.ZERO.compareTo(value.getMaxAmount()) < 0 && chargeAmount.compareTo(value.getMaxAmount()) > 0 ? value.getMaxAmount()
                                : (BigDecimal.ZERO.compareTo(value.getMinAmount()) < 0 && chargeAmount.compareTo(value.getMinAmount()) < 0 ? value.getMinAmount() : chargeAmount));
                        break;
                    case "T":
                        Object[] tiers = sortArray(value.getTiers().values().toArray(), true);
                        if (tiers.length > 0)
                        {
                            request.getCharge().setChargeAmount(((AXTier) tiers[tiers.length - 1]).getValue());
                            for (int i = tiers.length - 1; i >= 0; i--)
                            {
                                if (request.getAmount().compareTo(((AXTier) tiers[i]).getTierMax()) <= 0)
                                {
                                    request.getCharge().setChargeAmount(((AXTier) tiers[i]).getValue());
                                }
                            }
                        }
                        break;
                }

                Object[] waivers = sortArray(aXCharge.getWaivers().values().toArray(), true);
                if (waivers.length > 0)
                {
                    for (int i = waivers.length - 1; i >= 0; i--)
                    {
                        TCWaiver waiver = (TCWaiver) waivers[i];
                        if (0 == waiver.getProductId() || Objects.equals(waiver.getProductId(), request.getCharge().getChargeAccount().getProductId()) || Objects.equals(waiver.getProductId(), request.getContra().getProductId()))
                        {
                            if (0 == waiver.getProductId() || "A".equals(waiver.getMatchAccount()) || ("B".equals(waiver.getMatchAccount()) && Objects.equals(request.getCharge().getChargeAccount().getProductId(), request.getContra().getProductId())) || ("C".equals(waiver.getMatchAccount()) && Objects.equals(waiver.getProductId(), request.getCharge().getChargeAccount().getProductId())))
                            {
                                boolean waive = false;
                                CNActivity activity = getdClient().queryMonthActivity(request.getChannelId(), request.getAccount().getAccountNumber(), request.getTxnCode());
                                switch (waiver.getWaiverCondition())
                                {
                                    case "ALL":
                                        waive = true;
                                        break;
                                    case "MCL":
                                        waive = activity.getCount().compareTo(waiver.getThresholdValue()) <= 0;
                                        break;
                                    case "MCM":
                                        waive = activity.getCount().compareTo(waiver.getThresholdValue()) >= 0;
                                        break;
                                    case "MTL":
                                        waive = activity.getTotal().compareTo(waiver.getThresholdValue()) <= 0;
                                        break;
                                    case "MTM":
                                        waive = activity.getTotal().compareTo(waiver.getThresholdValue()) >= 0;
                                        break;
                                }
                                if (waive)
                                {
                                    request.getCharge().setChargeAmount((request.getCharge().getChargeAmount().multiply((HUNDRED.subtract(waiver.getWaivedPercentage())).divide(HUNDRED, 2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.DOWN));
                                    break;
                                }
                            }
                        }
                    }
                }
                prepareDeduction(request, value, splitAccounts);
            }
        }
    }

    private DBClient getdClient()
    {
        return getBox().getdClient();
    }

    private void prepareDeduction(AXRequest request, TCValue value, HashMap<String, String> splitAccounts)
    {
        String basis = "A";
        BigDecimal basisAmount = request.getCharge().getChargeAmount();
        BigDecimal balance = request.getCharge().getChargeAmount();
        HashMap<String, TCSplit> splitMap = new HashMap<>();

        for (TCDeduction deduction : sortArray(value.getDeductions().values().toArray(new TCDeduction[0]), true))
        {
            TCSplit split = new TCSplit();
            split.setReference(deduction.getBasis());

            split.setDescription((request.isReversal() ? "REV~" : "") + deduction.getDescription().trim());
            split.setAccount(splitAccounts.getOrDefault(deduction.getAccount(), (isLedger(deduction.getAccount()) ? getdClient().unmaskLedger(deduction.getAccount(), request.getBranch()).getAccountNumber() : deduction.getAccount())));

            if (!basis.equals(deduction.getBasis()))
            {
                basisAmount = balance;
            }
            basis = deduction.getBasis();

            switch (deduction.getValueType())
            {
                case "C":
                    split.setAmount(deduction.getValue());
                    break;
                case "P":
                    switch (deduction.getBasis())
                    {
                        case "A":
                            split.setAmount(basisAmount.multiply(deduction.getValue().divide(HUNDRED, 16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                            break;
                        case "B":
                            split.setAmount(basisAmount.multiply(deduction.getValue().divide(HUNDRED, 16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                            break;
                        case "C":
                            split.setAmount(basisAmount.multiply(deduction.getValue().divide(HUNDRED.add(deduction.getValue()), 16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                            break;
                        case "D":
                            split.setAmount(basisAmount.multiply(deduction.getValue().divide(HUNDRED.add(deduction.getValue()), 16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                            break;
                        default:
                            split.setAmount(basisAmount.multiply(deduction.getValue().divide(HUNDRED, 16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                            break;
                    }
                    break;
            }
            if (balance.compareTo(split.getAmount()) >= 0)
            {
                if (splitMap.containsKey(split.getAccount()))
                {
                    TCSplit spl = splitMap.get(split.getAccount());
                    spl.setAmount(spl.getAmount().add(split.getAmount()));
                    if (!Objects.equals(split.getDescription().toUpperCase(), spl.getDescription().toUpperCase()))
                    {
                        spl.setDescription(spl.getDescription() + " | " + split.getDescription());
                    }
                    splitMap.put(spl.getAccount(), spl);
                }
                else
                {
                    splitMap.put(split.getAccount(), split);
                }
                balance = balance.subtract(split.getAmount());
            }
        }

        splitMap.values().stream().forEach((spl)
                ->
        {
            request.getCharge().getSplitList().add(spl);
        });
        splitMap.clear();

        if (!request.getCharge().getSplitList().isEmpty() && BigDecimal.ZERO.compareTo(balance) < 0)
        {
            TCSplit split = new TCSplit();
            split.setReference("Z");

            split.setDescription(request.getCharge().getChargeNarration());
            split.setAccount(request.getCharge().getChargeLedger());

            split.setAmount(balance);
            request.getCharge().getSplitList().add(split);
        }
    }

    public String mapNarration(String narration, String DC)
    {
        DC = "C".equalsIgnoreCase(DC.trim()) ? "CR" : DC.trim();
        DC = "D".equalsIgnoreCase(DC.trim()) ? "DR" : DC.trim();
        if (narration.toUpperCase().contains("REV~") || narration.toUpperCase().contains("REVERSAL") || narration.toUpperCase().contains("RETURN"))
        {
            return "Reversal";
        }
        else if (narration.toUpperCase().contains("CHARGE"))
        {
            return "Charge";
        }
        else if (narration.toUpperCase().contains("LOAN"))
        {
            return "Loan Pay";
        }
        else if (narration.toUpperCase().contains("PAYMENT"))
        {
            return "Payment";
        }
        else if (narration.toUpperCase().contains("WITHDRAWAL"))
        {
            return "Withdrawal";
        }
        else if (narration.toUpperCase().contains("DEPOSIT"))
        {
            return "Deposit";
        }
        else if (narration.toUpperCase().contains("TRANSFER"))
        {
            return "Transfer";
        }
        return "CR".equalsIgnoreCase(DC) ? "Deposit" : "Withdrawal";
    }

    public String formatMsisdn(String msisdn)
    {
        if (!isBlank(msisdn))
        {
            msisdn = msisdn.replaceAll("[^\\d]", "");
            if (msisdn.length() == 9)
            {
                return APController.countryCode + msisdn;
            }
            else if (msisdn.startsWith("0"))
            {
                return APController.countryCode + msisdn.substring(1);
            }
        }
        return msisdn;
    }

    public String cleanText(String text)
    {
        String line;
        StringBuilder buffer = new StringBuilder();
        if (!isBlank(text))
        {
            try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
            {
                while ((line = bis.readLine()) != null)
                {
                    buffer.append(line.trim());
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(text, ex);
            }
        }
        return buffer.toString().replaceAll(">\\s+<", "><");
    }

    public String indentAllLines(String text, String indent)
    {
        String line = "", buffer = "";
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
        {
            while (line != null)
            {
                buffer += indent + line + "\r\n";
                line = bis.readLine();
            }
            return indent + buffer.trim();
        }
        catch (Exception ex)
        {
            return indent + buffer.trim();
        }
    }

    public <T> T cloneObject(Object source, Class<T> clazz)
    {
        T result = null;
        if (source != null)
        {
            try
            {
                result = clazz.newInstance();
                for (PropertyDescriptor propertyDesc : Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors())
                {
                    if (propertyDesc.getReadMethod() != null)
                    {
                        Object value = propertyDesc.getReadMethod().invoke(source);
                        if (propertyDesc.getWriteMethod() != null)
                        {
                            propertyDesc.getWriteMethod().invoke(result, value);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return result;
    }

    public void prepareAllScrollers(Component component)
    {
        if (component instanceof JScrollPane)
        {
            prepareScrollPane((JScrollPane) component);
        }
        if (component instanceof RootPaneContainer)
        {
            prepareAllScrollers(((RootPaneContainer) component).getContentPane());
        }
        if (component instanceof Container)
        {
            for (Component comp : ((Container) component).getComponents())
            {
                prepareAllScrollers(comp);
            }
        }
    }

    public void resetAllFields(Component component, Component... exceptions)
    {
        List<Component> list = Arrays.asList(exceptions);
        if (!list.contains(component))
        {
            if (component instanceof JPanel)
            {
                if (((JPanel) component).getBorder() != null && ((JPanel) component).getBorder() instanceof CQBorder)
                {
                    ((JCheckBox) ((CQBorder) ((JPanel) component).getBorder()).getComponent()).setSelected(false);
                    component.repaint();
                }
                for (Component comp : ((JPanel) component).getComponents())
                {
                    resetAllFields(comp, exceptions);
                }
            }
            else if (component instanceof JScrollPane)
            {
                resetAllFields(((JScrollPane) component).getViewport().getView(), exceptions);
            }
            else if (component instanceof JTextField)
            {
                ((JTextField) component).setText("");
            }
            else if (component instanceof JTextArea)
            {
                ((JTextArea) component).setText("");
            }
            else if (component instanceof JCheckBox && component.isEnabled())
            {
                ((JCheckBox) component).setSelected(false);
            }
            else if (component instanceof JComboBox && component.isEnabled() && ((JComboBox) component).getItemCount() > 0)
            {
                ((JComboBox) component).setSelectedIndex(0);
            }
            else if (component instanceof JDateChooser && ((JDateChooser) component).isEnabled())
            {
                ((JDateChooser) component).setDate(null);
            }
        }
    }

    public void setAllFields(Component component, boolean enabled)
    {
        if (component instanceof JPanel)
        {
            for (Component comp : ((JPanel) component).getComponents())
            {
                setAllFields(comp, enabled);
            }
        }
        if (component instanceof JTextField)
        {
            ((JTextField) component).setEditable(enabled);
        }
        if (component instanceof JTextArea)
        {
            ((JTextArea) component).setEditable(enabled);
        }
        if (component instanceof JCheckBox)
        {
            ((JCheckBox) component).setEnabled(enabled);
        }
        if (component instanceof JComboBox)
        {
            ((JComboBox) component).setEnabled(enabled);
        }
        if (component instanceof JDateChooser)
        {
            ((JDateChooser) component).setEnabled(enabled);
        }
    }

    public void setCheckBorder(JPanel panel, boolean selectBoxes)
    {
        if (panel.getBorder() instanceof TitledBorder)
        {
            TitledBorder border = (TitledBorder) panel.getBorder();
            JCheckBox checkBox = new JCheckBox(border.getTitle());
            checkBox.setForeground(border.getTitleColor());

            checkBox.setFont(border.getTitleFont());
            checkBox.addActionListener((ActionEvent e)
                    ->
            {
                boolean selected = checkBox.isSelected();
                for (Component comp : panel.getComponents())
                {
                    if (selectBoxes && comp instanceof JCheckBox)
                    {
                        ((JCheckBox) comp).setSelected(selected);
                    }
                    else
                    {
                        comp.setEnabled(selected);
                    }
                }
            });
            checkBox.setFocusPainted(false);
            panel.setBorder(new CQBorder(checkBox, panel, border.getBorder()));
        }
    }

    public String convertToXml(Object object, boolean formatted)
    {
        if (!isBlank(object))
        {
            try
            {
                Class clazz = object instanceof JAXBElement ? ((JAXBElement) object).getDeclaredType() : object.getClass();
                Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);

                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                StringWriter writer = new StringWriter();
                marshaller.marshal(object, writer);
                return cleanXmlXters(writer.toString().trim());
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return null;
    }

    public <T> T convertXmlToObject(String xml, String rootElement, Class<T> clazz)
    {
        try
        {
            return (T) JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(new ByteArrayInputStream(removeNameSpaces(extractXmlValue(xml, rootElement, true, String.class)).getBytes(StandardCharsets.ISO_8859_1)));
        }
        catch (Exception ex)
        {
            getLog().logEvent(xml, ex);
        }
        return null;
    }

    public <T> T createList(String csvList, Class<T> clazz)
    {
        T list = null;

        try
        {
            list = clazz.newInstance();
            if (csvList != null)
            {
                for (String listItem : csvList.replaceAll(";", ",").split(","))
                {
                    ((List) list).add(listItem.trim());
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(csvList, ex);
        }
        return list;
    }

    public String createCsvList(ArrayList list)
    {
        StringBuilder buffer = new StringBuilder();
        if (list != null)
        {
            list.stream().forEach((item)
                    ->
            {
                buffer.append(buffer.length() > 0 ? "," + item : item);
            });
        }
        return buffer.toString();
    }

    public String formatDate(SimpleDateFormat format, Date date)
    {
        return !isBlank(date) ? format.format(date) : null;
    }

    public ArrayList<String> createArrayList(String csvList)
    {
        ArrayList<String> list = new ArrayList<>();
        if (!isBlank(csvList))
        {
            for (String listItem : csvList.replaceAll(";", ",").split(","))
            {
                if (!isBlank(listItem) && !list.contains(listItem))
                {
                    list.add(listItem.trim());
                }
            }
        }
        return list;
    }

    public String cleanCSVList(String csvList)
    {
        ArrayList<String> list = new ArrayList<>();
        if (!isBlank(csvList))
        {
            for (String listItem : csvList.replaceAll(";", ",").split(","))
            {
                if (!isBlank(listItem) && !list.contains(listItem))
                {
                    list.add(listItem.trim());
                }
            }
        }
        return createCSVList(list);
    }

    public ArrayList<AXPage> paginateMessage(String message, int asciiPageSize, String nextText)
    {
        String brx = "\n~. ,/ ";
        AXPage prevPage = new AXPage();
        ArrayList<AXPage> pages = new ArrayList<>();

        int ni, pg = 1, tl = nextText.length(), ps = isPureAscii(message) ? asciiPageSize : asciiPageSize / 2;
        while ((ni = nextPageBreak(message.trim(), brx, 0, ps, tl, 0)) > 0)
        {
            AXPage page = new AXPage(pg++, (message.substring(0, ni).trim() + (ni < message.length() ? nextText : "")));
            message = message.substring(ni).trim();
            prevPage.setNextPage(page);

            pages.add(page);
            prevPage = page;
        }
        return pages;
    }

    private int nextPageBreak(String text, String brx, int ni, int ps, int tl, int pi)
    {
        int in, def = text.length() > ps ? ps - tl : text.length();
        if (text.length() > ps && brx.length() > 0)
        {
            String ch = brx.substring(0, 1);
            while ((in = text.indexOf(ch, ni)) < ps - tl && in >= 0)
            {
                ni = in + 1;
            }
            ni = pi < ni && pi + 3 >= ni ? pi : ni;
            if (ni < (int) (ps * 0.85))
            {
                return brx.length() > 1 ? nextPageBreak(text, brx.substring(1), ni, ps, tl, ni) : def;
            }
            return ni <= 0 ? def : ni;
        }
        return def;
    }

    public String createCSVList(ArrayList list)
    {
        StringBuilder buffer = new StringBuilder();
        if (list != null)
        {
            list.stream().forEach((item)
                    ->
            {
                buffer.append(buffer.length() > 0 ? "," + item : item);
            });
        }
        return buffer.toString();
    }

    public String truncateName(String name, int count)
    {
        if (name != null && !name.trim().equals(""))
        {
            String shortName = "";
            String[] names = name.split("\\s+");
            for (int i = 0; i < count && i < names.length; i++)
            {
                shortName += names[i] + " ";
            }
            return capitalize(shortName.trim());
        }
        return name;
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

    public String capitalize(String text, int minLen)
    {
        if (text != null)
        {
            StringBuilder builder = new StringBuilder();
            for (String word : text.split("\\s"))
            {
                builder.append(word.length() > minLen ? capitalize(word) : word).append(" ");
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

    public String protectField(String fieldValue, int preSkip, int postSkip)
    {
        if (!isBlank(fieldValue))
        {
            for (int i = preSkip; i < fieldValue.length() - postSkip; i++)
            {
                fieldValue = fieldValue.substring(0, i) + "X" + fieldValue.substring(i + 1);
            }
        }
        return fieldValue;
    }

    public String spaceWords(String text)
    {
        if (!isBlank(text))
        {
            char p = ' ';
            StringBuilder builder = new StringBuilder();
            for (char c : text.toCharArray())
            {
                builder.append((Character.isUpperCase(c) && !Character.isUpperCase(p)) || (Character.isDigit(c) && !Character.isDigit(p)) || (!Character.isDigit(c) && Character.isDigit(p)) ? (" " + c) : (builder.length() == 0 ? String.valueOf(c).toUpperCase() : c));
                p = c;
            }
            return builder.toString().trim();
        }
        return text;
    }

    public String replaceAll(String message, String holder, String replacement)
    {
        if (!isBlank(message))
        {
            replacement = replacement == null ? "<>" : replacement;
            while (message.contains(holder) && !replacement.equals(holder))
            {
                message = message.replace(holder, replacement);
            }
        }
        return message;
    }

    public boolean validateFields(JDialog dialog, JPanel panel, Component... exceptions)
    {
        boolean valid = true;
        List<Component> list = Arrays.asList(exceptions);
        for (Component component : panel.getComponents())
        {
            if (component instanceof JComponent && component.isEnabled() && !list.contains(component))
            {
                valid = (component instanceof JPanel && !(component instanceof JDateChooser)) ? validateFields(dialog, (JPanel) component, exceptions) : validateValue(dialog, (JComponent) component);
            }
            if (!valid)
            {
                break;
            }
        }
        return valid;
    }

    public boolean validateValue(JDialog dialog, JComponent component)
    {
        boolean invalid;
        if (invalid = (component instanceof JDateChooser ? isBlank(((JDateChooser) component).getDate())
                : (component instanceof JTextField ? isBlank(((JTextField) component).getText()) && ((JTextField) component).isEditable()
                        : (component instanceof JComboBox ? isBlank(((JComboBox) component).getSelectedItem()) : false))))
        {
            JOptionPane.showMessageDialog(dialog, "Field Required [ " + component.getToolTipText() + " ]", "Missing Field", JOptionPane.ERROR_MESSAGE);
            component.requestFocus();
        }
        return !invalid;
    }

    public <T> T convertToType(String variable, Class<T> clazz)
    {
        return convertToType(variable, clazz, null);
    }

    public <T> T convertToType(String variable, Class<T> clazz, T defaultValue)
    {
        try
        {
            return (T) clazz.getConstructor(String.class).newInstance(variable.trim());
        }
        catch (Exception ex)
        {
            return defaultValue;
        }
    }

    public String cleanSpaces(String text)
    {
        if (!isBlank(text))
        {
            return text.replaceAll("\\s+", " ").trim();
        }
        return text;
    }

    public String cleanField(String text, boolean space)
    {
        if (!isBlank(text))
        {
            String prev = "";
            StringBuilder buffer = new StringBuilder();
            for (String t : (space ? spaceWords(text) : text).split("\\s+"))
            {
                if (!prev.equalsIgnoreCase(t))
                {
                    buffer.append(" ").append(t);
                    prev = t;
                }
            }
            return buffer.toString().trim();
        }
        return text;
    }

    public String replaceXmlXters(String xml, boolean escape)
    {
        HashMap<String, String> xters = escape ? escapeXters : unescapeXters;
        for (String key : xters.keySet())
        {
            xml = xml.replaceAll(key, xters.get(key));
        }
        return xml;
    }

    public void expandAllNodes(JTree tree, Class clazz)
    {
        int selRow = 0;
        tree.updateUI();
        for (int i = 0; i < tree.getRowCount(); i++)
        {
            selRow = (selRow == 0 && clazz.isInstance(((DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent()).getUserObject())) ? i : selRow;
            tree.expandRow(i);
        }
        tree.setSelectionRow(selRow);
        tree.scrollPathToVisible(tree.getPathForRow(selRow));
        updateTreeUI(tree, true);
    }

    public void selectTreeNode(JTree tree, Object userObject)
    {
        if (!isBlank(userObject))
        {
            DefaultMutableTreeNode currentNode = ((DefaultMutableTreeNode) tree.getModel().getRoot()).getFirstLeaf();
            while (currentNode != null)
            {
                if (Objects.equals(currentNode.getUserObject(), userObject) || Objects.equals(currentNode.getUserObject().toString().split("~")[0].toUpperCase().trim(), String.valueOf(userObject).split("~")[0].toUpperCase().trim()))
                {
                    if (!Objects.equals(currentNode.getUserObject(), getSelectedObject(tree, userObject.getClass())))
                    {
                        tree.scrollPathToVisible(new TreePath(currentNode.getPath()));
                        tree.setSelectionPath(new TreePath(currentNode.getPath()));
                        updateTreeUI(tree, false);
                    }
                    break;
                }
                DefaultMutableTreeNode nextNode = currentNode.isLeaf() ? currentNode.getNextSibling() : (DefaultMutableTreeNode) currentNode.getFirstChild();
                currentNode = nextNode == null ? ((DefaultMutableTreeNode) currentNode.getParent()).getNextSibling() : nextNode;
            }
        }
    }

    public <T> T getSelectedObject(JTree tree, Class<T> clazz)
    {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null)
        {
            if (clazz.isInstance(selectedNode.getUserObject()))
            {
                return (T) selectedNode.getUserObject();
            }
        }
        return null;
    }

    public String extractXmlValue(String text, String tag, boolean includeTag)
    {
        if (!isBlank(text) && !isBlank(tag))
        {
            int elementStart = text.toLowerCase().indexOf("<" + tag.toLowerCase()), valueEnd = text.toLowerCase().indexOf("</" + tag.toLowerCase());
            if (elementStart >= 0 && elementStart < valueEnd)
            {
                return includeTag ? text.substring(elementStart, (text.indexOf(">", valueEnd) + 1)) : text.substring((text.indexOf(">", elementStart) + 1), valueEnd);
            }
        }
        return isBlank(tag) ? text : null;
    }

    public String extractTxnId(String text, String tag, boolean includeTag)
    {
        if (!isBlank(text) && !isBlank(tag))
        {
            int elementStart = text.toLowerCase().indexOf("<" + tag.toLowerCase()), valueEnd = text.toLowerCase().indexOf("</" + tag.toLowerCase());
            if (elementStart >= 0 && elementStart < valueEnd)
            {
                return includeTag ? text.substring(elementStart, (text.indexOf(">", valueEnd) + 1)) : text.substring((text.indexOf(">", elementStart) + 1), valueEnd);
            }
        }
        return isBlank(tag) ? text : (isNumber(text) ? text : null);
    }

    public TreeMap<String, String> extractIsoSubFields(String extFieldText)
    {
        TreeMap<String, String> dataMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (!isBlank(extFieldText))
        {
            extFieldText = extFieldText.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&quot;", "\"");
            while (extFieldText.length() > 0)
            {
                int lenWidth = Integer.parseInt(extFieldText.substring(0, 1));
                extFieldText = extFieldText.substring(1);
                int length = Integer.parseInt(extFieldText.substring(0, lenWidth));

                extFieldText = extFieldText.substring(lenWidth);
                String key = extFieldText.substring(0, length);
                extFieldText = extFieldText.substring(length);

                lenWidth = Integer.parseInt(extFieldText.substring(0, 1));
                extFieldText = extFieldText.substring(1);
                length = Integer.parseInt(extFieldText.substring(0, lenWidth));

                extFieldText = extFieldText.substring(lenWidth);
                String value = extFieldText.substring(0, length);
                extFieldText = extFieldText.substring(length);
                dataMap.put(key, value);
            }
        }
        return dataMap;
    }

    public String errorCode(Exception event)
    {
        String error = AXResult.FAILED.name();
        try
        {

            if (event instanceof com.neptunesoftware.supernova.ws.server.account.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.account.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.account.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.channeladmin.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.channeladmin.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.creditapp.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.creditapp.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.customer.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.customer.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.loanaccount.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.loanaccount.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.account.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.transaction.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.transaction.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.transfer.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.transfer.XAPIException) event).getFaultInfo().getErrorCode();
            }
            else if (event instanceof com.neptunesoftware.supernova.ws.server.txnprocess.XAPIException)
            {
                error = ((com.neptunesoftware.supernova.ws.server.txnprocess.XAPIException) event).getFaultInfo().getErrorCode();
            }
            return error;

        }
        catch (Exception e)
        {
            getLog().logEvent(e);
        }
        return error;
    }

    public AXSetting createSetting(Field field, String module)
    {
        AXSetting setting = new AXSetting();
        try
        {
            Class clazz = field.getType();
            Object value = field.get(null);

            if (!isBlank(value))
            {
                if (clazz == ArrayList.class)
                {
                    value = createCSVList((ArrayList) value);
                }
                else if (clazz == CNAccount.class)
                {
                    value = ((CNAccount) value).getAccountNumber();
                }
                else if (clazz == CNBranch.class)
                {
                    value = ((CNBranch) value).getBuId();
                }
            }

            setting.setChannel(module);
            setting.setCode(capitalize(field.getName(), false));
            setting.setDescription(spaceWords(field.getName()));

            setting.setValue(String.valueOf(value));
            setting.setSysDate(new Date());
            setting.setSysUser("SYSTEM");
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return setting;
    }

    public <T> T extractXmlValue(String text, String tag, boolean includeTag, Class<T> clazz)
    {
        if (!isBlank(text) && !isBlank(tag))
        {
            int elementStart = text.toLowerCase().indexOf("<" + tag.toLowerCase()), valueEnd = text.toLowerCase().indexOf("</" + tag.toLowerCase());
            if (elementStart >= 0 && elementStart < valueEnd)
            {
                return convertToType((includeTag ? text.substring(elementStart, (text.indexOf(">", valueEnd) + 1)) : text.substring((text.indexOf(">", elementStart) + 1), valueEnd)), clazz);
            }
        }
        return convertToType((isBlank(tag) ? text : null), clazz);
    }

    public ArrayList<String> extractPlaceHolders(String text)
    {
        ArrayList<String> holdersList = new ArrayList<>();
        Matcher matcher = holderPattern.matcher(String.valueOf(text));
        while (matcher.find())
        {
            holdersList.add(matcher.group(0));
        }
        return holdersList;
    }

    public void formatDecimalValue(JTextField field)
    {
        BigDecimal value = convertToType(field.getText().trim(), BigDecimal.class);
        if (!isBlank(value))
        {
            field.setText(formatDecimal(value).toPlainString());
        }
    }

    public String lastText(String text, int count)
    {
        int length = text.length();
        if (length > count)
        {
            int startIndex = length - count;
            return text.substring(startIndex);
        }
        return text;
    }

    public java.sql.Date parseddMMyyyyDate(String date)
    {
        try
        {
            return !isBlank(date) ? new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(date).getTime()) : null;
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return null;
    }

    public LocalDate parseddMMyyyyLocalDate(String date)
    {
        try
        {
            return !isBlank(date) ? LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")) : LocalDate.now();
        }
        catch (Exception ex)
        {
            return LocalDate.now();
        }
    }

    public void selectBoxValue(JComboBox box, String code)
    {
        for (int i = 0; i < box.getItemCount(); i++)
        {
            if (box.getItemAt(i).toString().split("~")[0].trim().equalsIgnoreCase(code))
            {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.setSelectedItem(code);
    }

    public void loadProperties(File file, AXProperties properties)
    {
        try (InputStream pin = new FileInputStream(file))
        {
            properties.loadFromXML(pin);
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    public boolean isNumber(String text)
    {
        try
        {
            return convertToType(text, Double.class) != null;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public boolean isPositiveNumber(String text)
    {
        try
        {
            return convertToType(text, Double.class) >= 0;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public void replacePrefixes(SOAPElement parent, SOAPElement element, HashMap<String, String> map) throws SOAPException
    {
        if (element != null)
        {
            ArrayList list = new ArrayList();
            Iterator<QName> ita = element.getAllAttributesAsQNames();
            while (ita.hasNext())
            {
                QName qname = ita.next();
                String prefix = qname.getPrefix(), uri = element.getNamespaceURI(prefix);
                if (!map.containsKey(uri))
                {
                    map.put(uri, prefix);
                }
                if (element != parent || !Objects.equals(map.get(uri), prefix))
                {
                    list.add(qname);
                }
            }

            Iterator<String> itx = element.getNamespacePrefixes();
            while (itx.hasNext())
            {
                String prefix = itx.next();
                String uri = element.getNamespaceURI(prefix);
                if (!map.containsKey(uri))
                {
                    map.put(uri, prefix);
                }
                if (element != parent || !Objects.equals(map.get(uri), prefix))
                {
                    list.add(prefix);
                }
            }

            String uri = element.getNamespaceURI(), prefix = map.get(uri);
            if (parent.getNamespaceURI(prefix) == null)
            {
                parent.addNamespaceDeclaration(prefix, uri);
            }

            element.setPrefix(prefix);

            list.stream().forEach((o)
                    ->
            {
                if (o instanceof QName)
                {
                    element.removeNamespaceDeclaration(((QName) o).getPrefix());
                    element.removeAttribute((QName) o);
                }
                else if (o instanceof String)
                {
                    element.removeNamespaceDeclaration((String) o);
                }
            });

            Iterator<String> children = element.getChildElements();
            while (children.hasNext())
            {
                Object child = children.next();
                if (child instanceof SOAPElement)
                {
                    replacePrefixes(parent, (SOAPElement) child, map);
                }
            }
        }
    }

    public void replacePrefixes(SOAPElement element, HashMap<String, String> namespaces, ArrayList<String> prefixes) throws SOAPException
    {
        if (element != null)
        {
            Iterator<String> it = element.getNamespacePrefixes();
            ArrayList<String> ownPrefixes = new ArrayList<>(prefixes);
            while (it.hasNext())
            {
                ownPrefixes.add(it.next());
            }

            for (String prefix : ownPrefixes)
            {
                String uri = element.getNamespaceURI(prefix);
                if (namespaces.containsKey(uri))
                {
                    String newPrefix = namespaces.get(uri);
                    element.removeNamespaceDeclaration(prefix);
                    element.addNamespaceDeclaration(newPrefix, uri);
                    namespaces.put(prefix, newPrefix);
                }
                else
                {
                    namespaces.put(uri, prefix);
                }
            }
            namespaces.keySet().stream().forEach((key)
                    ->
            {
                ownPrefixes.remove(key);
            });
            if (element.getPrefix() != null)
            {
                element.setPrefix(namespaces.getOrDefault(element.getPrefix(), element.getPrefix()));
            }

            Iterator<String> children = element.getChildElements();
            while (children.hasNext())
            {
                Object child = children.next();
                if (child instanceof SOAPElement)
                {
                    replacePrefixes((SOAPElement) child, namespaces, ownPrefixes);
                }
            }
        }
    }

    public String nextCode(TreeMap list, String prefix)
    {
        int i = 1;
        String code = prefix + String.format("%02d", i);
        while (list.containsKey(code))
        {
            code = prefix + String.format("%02d", ++i);
        }
        return code;
    }

    public void updateTreeUI(JTree tree, boolean resetScrollers)
    {
        EventQueue.invokeLater(()
                ->
        {
            Container container = SwingUtilities.getAncestorOfClass(JScrollPane.class, tree);
            if (container instanceof JScrollPane)
            {
                JScrollPane scroller = (JScrollPane) container;
                scroller.getHorizontalScrollBar().setValue(0);
                if (resetScrollers)
                {
                    scroller.getVerticalScrollBar().setValue(0);
                }
                scroller.setBorder(null);
            }
            tree.updateUI();
        });
    }

    public boolean isSettings(Field field)
    {
        if (Map.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType)
        {
            return Objects.equals(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1], AXSetting.class);
        }
        return false;
    }

    public void prepareScrollPane(JScrollPane scroller)
    {
        JButton button = new JButton(new javax.swing.ImageIcon(getClass().getResource("/Ruby/ximg/corner.png")));
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.addActionListener((ActionEvent e)
                ->
        {
            JScrollBar scrollBar = scroller.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });
        scroller.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, button);
    }

    public void pauseThread(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception ie)
        {
            ie = null;
        }
    }

    public CNAccount findAccount(ArrayList<CNAccount> accounts, String currency)
    {
        for (CNAccount account : accounts)
        {
            if (Objects.equals(account.getCurrency().getCurrencyCode(), currency))
            {
                return account;
            }
        }
        return new CNAccount();
    }

    public boolean hasAccount(ArrayList<CNAccount> accounts, String currency)
    {
        return accounts.stream().anyMatch((account) -> (Objects.equals(account.getCurrency().getCurrencyCode(), currency)));
    }

    public XMLGregorianCalendar createXmlGregorianCalendar(Date date)
    {
        return createXmlGregorianCalendar(date, true);
    }

    public XMLGregorianCalendar createXmlGregorianCalendar(Date date, boolean includeTime)
    {
        try
        {
            if (!isBlank(date))
            {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
                if (!includeTime)
                {
                    xmlCalendar.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
                    xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                }
                return xmlCalendar;
            }
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return null;
    }

    public <T> ArrayList<T> sortArrayList(ArrayList<T> arrayList, boolean ascending)
    {
        Collections.sort(arrayList, ascending ? ((Comparator<T>) (T o1, T o2) -> ((Comparable) o1).compareTo(o2)) : ((Comparator<T>) (T o1, T o2) -> ((Comparable) o2).compareTo(o1)));
        return arrayList;
    }

    public <T> T[] sortArray(T[] array, boolean ascending)
    {
        Arrays.sort(array, ascending ? ((Comparator<T>) (T o1, T o2) -> ((Comparable) o1).compareTo(o2)) : ((Comparator<T>) (T o1, T o2) -> ((Comparable) o2).compareTo(o1)));
        return array;
    }

    public String formatEquinoxAccount(String acctNo)
    {
        acctNo = acctNo != null ? String.valueOf(acctNo.replaceAll("[^\\d]", "")) : String.valueOf(acctNo);
        return acctNo.trim().length() < 5 ? acctNo : acctNo.trim().substring(0, 3) + "-" + acctNo.trim().substring(3);
    }

    public <T> T extractValue(JAXBElement<T> element)
    {
        return !isBlank(element) ? (T) element.getValue() : null;
    }

    public String prepareDuration(Long startTime)
    {
        return (System.currentTimeMillis() - startTime) + " Ms ~ ";
    }

    public String trimRight(String text)
    {
        return rTrimPattern.matcher(text).replaceAll("").replaceAll("\r\n", "");
    }

    public String trimLeft(String text)
    {
        return lTrimPattern.matcher(text).replaceAll("").replaceAll("\r\n", "");
    }

    public String formatIsoAmount(BigDecimal value)
    {
        return String.format("%012d", value.abs().setScale(2, RoundingMode.DOWN).multiply(AXConstant.HUNDRED).longValue());
    }

    public BigDecimal formatDecimal(BigDecimal value)
    {
        return !isBlank(value) ? value.setScale(2, RoundingMode.HALF_UP) : value;
    }

    public String formatDecimalText(BigDecimal value)
    {
        return !isBlank(value) ? value.setScale(2, RoundingMode.HALF_UP).toPlainString() : null;
    }

    public boolean validatePercentage(BigDecimal perc)
    {
        return !isBlank(perc) ? (BigDecimal.ZERO.compareTo(perc) <= 0 || AXConstant.HUNDRED.compareTo(perc) >= 0) : false;
    }

    public boolean validateAmount(BigDecimal amount, boolean allowZero)
    {
        return !isBlank(amount) && BigDecimal.ZERO.compareTo(amount) <= (allowZero ? 0 : -1);
    }

    public boolean validateLimit(BigDecimal amount, BigDecimal limit)
    {
        return !isBlank(amount) && !isBlank(limit) && limit.compareTo(amount) >= 0;
    }

    public String getOrdinalFor(int day)
    {
        int modTen = day % 10;
        return day + (((day % 100) - (modTen) != 10) && modTen >= 1 && modTen <= 3 ? (modTen == 2 ? "nd" : (modTen == 3 ? "rd" : "st")) : "th");
    }

    public String convertToOracleDate(Date date)
    {
        return !isBlank(date) ? "TO_DATE('" + dateFormat.format(date) + "','DD-MM-YYYY')" : null;
    }

    public String convertToOracleTime(Date date)
    {
        return !isBlank(date) ? "TO_DATE('" + timeFormat.format(date) + "','DD-MM-YYYY HH24:MI:SS')" : null;
    }

    public String formatDisplayDate(Date date)
    {
        return !isBlank(date) ? displayDateFormat.format(date) : "<>";
    }

    public String convertToOracleDate(LocalDate date)
    {
        return !isBlank(date) ? "TO_DATE('" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "','DD-MM-YYYY')" : null;
    }

    public <T> T getBoxValue(JComboBox box, Class<T> clazz)
    {
        return convertToType(getBoxValue(box), clazz);
    }

    public String getBoxValue(JComboBox box)
    {
        return !isBlank(box.getSelectedItem()) ? box.getSelectedItem().toString().split("~")[0].trim() : "";
    }

    public boolean matchName(String name)
    {
        return !isBlank(name) ? namePattern.matcher(name).matches() : false;
    }

    public boolean isYes(String setting)
    {
        return checkBlank(setting, "No").toUpperCase().startsWith("Y");
    }

    public BigDecimal checkZeroDivisor(BigDecimal decimal)
    {
        return BigDecimal.ZERO.compareTo(decimal) >= 0 ? BigDecimal.ONE : decimal;
    }

    public boolean isPureAscii(String message)
    {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(message);
    }

    public String truncateText(String text, int length)
    {
        return !isBlank(text) ? (text.length() > length ? text.substring(0, length) : text) : text;
    }

    public Date convertToDate(XMLGregorianCalendar calendar)
    {
        return !isBlank(calendar) ? calendar.toGregorianCalendar().getTime() : null;
    }

    public LocalDate convertToLocalDate(XMLGregorianCalendar calendar)
    {
        return !isBlank(calendar) ? toLocalDate(calendar.toGregorianCalendar().getTime()) : null;
    }

    public LocalDate toLocalDate(Date date)
    {
        return new java.util.Date(date.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Date toDate(LocalDate localDate)
    {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Long monthsBetween(LocalDate startDate, LocalDate endDate)
    {
        return Math.abs(Period.between(startDate, endDate).toTotalMonths());
    }

    public Long yearsToDate(LocalDate startDate)
    {
        return Period.between(startDate, getSystemDate()).toTotalMonths() / 12;
    }

    public String getDayOfWeek(LocalDate localDate)
    {
        return capitalize(localDate.getDayOfWeek().toString());
    }

    public LocalDate nextDayOfWeek(DayOfWeek dayOfWeek)
    {
        return getSystemDate().with(nextOrSame(dayOfWeek));
    }

    public LocalDate nextDayOfMonth(int DAY_OF_MONTH)
    {
        return getSystemDate().plusMonths(getSystemDate().getDayOfMonth() > DAY_OF_MONTH ? 1 : 0).withDayOfMonth(DAY_OF_MONTH);
    }

    public LocalDate datePlusDays(Date date, int days)
    {
        return toLocalDate(date).plusDays(days);
    }

    public LocalDate systemDatePlusDays(int days)
    {
        return getSystemDate().plusDays(days);
    }

    public LocalDate systemDatePlusWeeks(int weeks)
    {
        return getSystemDate().plusWeeks(weeks);
    }

    public LocalDate systemDatePlusMonths(int months)
    {
        return getSystemDate().plusMonths(months);
    }

    public LocalDate systemDatePlusYears(int years)
    {
        return getSystemDate().plusYears(years);
    }

    public LocalDate systemDateMinusDays(int days)
    {
        return getSystemDate().minusDays(days);
    }

    public LocalDate systemDateMinusWeeks(int weeks)
    {
        return getSystemDate().minusWeeks(weeks);
    }

    public LocalDate systemDateMinusMonths(int months)
    {
        return getSystemDate().minusMonths(months);
    }

    public LocalDate systemDateMinusYears(int years)
    {
        return getSystemDate().minusYears(years);
    }

    public String convertToString(Object object)
    {
        return convertToString(object, null).trim();
    }

    public String formatAmount(BigDecimal amount)
    {
        return !isBlank(amount) ? decimalFormat.format(amount) : "<>";
    }

    public String formatAmount(BigDecimal amount, CNCurrency cNCurrency, String nillValue)
    {
        return !isBlank(amount) ? amount.abs().setScale(cNCurrency.getDecimalPlaces(), RoundingMode.DOWN).toPlainString() : nillValue;
    }

    public String escapeXmlXters(String xmlText)
    {
        return xmlText.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("\'", "&apos;").trim();
    }

    public String cleanXmlXters(String xmlText)
    {
        return xmlText.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"").replaceAll("&apos;", "\'").trim();
    }

    public String removeNameSpaces(String xml)
    {
        return xml != null ? xml.replaceAll("\\s*xmlns.*?=\".*?\"", "") : xml;
    }

    public String convertToXml(Object object, String rootElement, boolean formatted)
    {
        return object != null ? convertToXml(new JAXBElement(new QName(rootElement), object.getClass(), object), formatted) : null;
    }

    public String unwrapOptionalText(String text, boolean include)
    {
        return include ? text.replaceAll("[\\[\\]]", "") : text.replaceAll("\\[.*?\\]", "").replaceAll("\\s+", " ");
    }

    public <T> T convertXmlToObject(String xml, Class<T> clazz)
    {
        return convertXmlToObject(xml, null, clazz);
    }

    public <T> T cloneObject(Object source)
    {
        return source != null ? cloneObject(source, (Class<T>) source.getClass()) : null;
    }

    public String cleanUssdText(String ussdText)
    {
        return ussdText.replaceAll("\n", "~").replaceAll("~\\s*~", "~").replaceAll("\\s+", " ").replaceAll("~", "\n").trim().replaceAll("\n", "~");
    }

    public CNAccount verifyAccount(CNAccount account, CNAccount defaultAccount)
    {
        return isBlank(account) || isBlank(account.getAccountNumber()) ? defaultAccount : account;
    }

    public boolean isLedger(String accountNumber)
    {
        return !isBlank(accountNumber) && accountNumber.split("-").length >= 2;
    }

    public boolean isBlank(Object object)
    {
        return object == null || "".equals(String.valueOf(object).trim()) || "null".equals(String.valueOf(object).trim()) || String.valueOf(object).trim().toLowerCase().contains("---select");
    }

    public LocalDate getSystemDate()
    {
        return toLocalDate(getdClient().getSystemDate());
    }

    public LocalDate getProcessingDate()
    {
        return toLocalDate(getdClient().getProcessingDate());
    }

    public <T> T checkBlank(T value, T nillValue)
    {
        return !isBlank(value) ? value : nillValue;
    }

    public String checkLength(String value, Integer maxLen)
    {
        return String.valueOf(value).length() > maxLen ? value.substring(value.length() - maxLen) : value;
    }

    public String firstName(String name)
    {
        return name != null && name.trim().length() > 0 ? capitalize(name.trim().split("\\s")[0]) : name;
    }

    public boolean isCsvList(String value)
    {
        return !isBlank(value) ? value.split(",").length > 1 : false;
    }

    public String blankNull(String value)
    {
        return checkBlank(value, "");
    }

    public String yesNo(boolean isYes)
    {
        return isYes ? "Yes" : "No";
    }

    public String capitalize(String text)
    {
        return capitalize(text, true);
    }

    public int randomInteger()
    {
        return (int) (Math.random() * 99627);
    }

    public String padText(String text, int length, boolean right)
    {
        return String.format("%" + (right ? "-" : "") + length + "s", text);
    }

    public String padNumber(Integer number, int length)
    {
        return String.format("%0" + length + "d", number);
    }

    public String nextCode(TreeMap list)
    {
        return nextCode(list, "");
    }

    /**
     * @return the logger
     */
    public APLog getLog()
    {
        return getBox().getLog();
    }

    /**
     * @return the box
     */
    public ATBox getBox()
    {
        return box;
    }

    /**
     * @param box the box to set
     */
    public void setBox(ATBox box)
    {
        this.box = box;
    }
}
