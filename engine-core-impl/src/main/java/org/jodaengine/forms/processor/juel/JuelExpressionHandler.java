package org.jodaengine.forms.processor.juel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.StartTag;

import org.jodaengine.process.instance.ProcessInstanceContext;
import org.jodaengine.resource.allocation.Form;
import org.jodaengine.resource.allocation.JodaFormField;
import org.jodaengine.util.juel.ProcessELContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.RootPropertyResolver;
import de.odysseus.el.util.SimpleResolver;

/**
 * The Class JUELExpressionHandler. Tries to evaluate all JUEL conditions for the forms, if they exist.
 */
public class JuelExpressionHandler extends AbstractFormFieldHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void setInternally(Form form,
                                 List<FormField> formFields,
                                 ProcessInstanceContext context,
                                 OutputDocument output) {

        ExpressionFactory factory = new ExpressionFactoryImpl();
        ELContext elContext = new ProcessELContext(context);

        Iterator<FormField> it = formFields.iterator();
        while (it.hasNext()) {
            FormField field = it.next();
            StartTag tag = field.getFormControl().getFirstStartTag();
            Attributes attributes = tag.getAttributes();

            // get the corresponding jodaField
            JodaFormField jodaField = form.getFormField(attributes.getValue("name"));

            // replace the value attribute with the result of the readExpression (if it exists)
            String readExpression = jodaField.getReadExpression();
            if (readExpression != null) {
                ValueExpression e = factory.createValueExpression(elContext, readExpression, String.class);
                try {
                    String result = (String) e.getValue(elContext);
                    Map<String, String> replacements = output.replace(attributes, false);

                    replacements.put("value", result);

                    // remove the formField from the formFields list, as it has been processed sucessfully.
                    it.remove();
                } catch (PropertyNotFoundException exception) {
                    // requested variable does not exist.
                    // do not change the value of the input field.
                    logger.debug("The expression {} cannot be resolved.", readExpression);
                }

            }

        }

    }

    @Override
    protected void readInternally(Map<String, String> enteredValues, Form form, ProcessInstanceContext context) {

        ExpressionFactory factory = new ExpressionFactoryImpl();
        ELContext elContext = new ProcessELContext(context);

        for (Entry<String, String> entry : enteredValues.entrySet()) {
            String fieldName = entry.getKey();
            String enteredValue = entry.getValue();

            JodaFormField formField = form.getFormField(fieldName);
            Object objectToSet = convertStringInput(enteredValue, formField.getDataClazz());
            String expressionToEvaluate = formField.getWriteExpression();

            // only set the variable, if it has been specified in the JodaFormField
            if (expressionToEvaluate != null) {
                ValueExpression e = factory.createValueExpression(elContext, expressionToEvaluate, String.class);
                try {
                    e.setValue(elContext, objectToSet);

                    // remove the formField from the formFields list, as it has been processed sucessfully.
                    enteredValues.remove(fieldName);
                } catch (PropertyNotFoundException exception) {
                    // requested variable does not exist.
                    // do not change the value of the input field.
                    logger.debug("The expression {} cannot be resolved.", expressionToEvaluate);
                } catch (ELException exception) {
                    logger.debug("The expression {} cannot be set because of: {}.", expressionToEvaluate,
                        exception.getMessage());
                }

            }            

        }
        
        // write all set root properties to the context.
        SimpleResolver resolver = (SimpleResolver) elContext.getELResolver();
        RootPropertyResolver rootResolver = resolver.getRootPropertyResolver();
        
        Iterator<String> it = rootResolver.properties().iterator();
        while (it.hasNext()) {
            String propertyName = it.next();
            Object propertyValue = rootResolver.getProperty(propertyName);
            context.setVariable(propertyName, propertyValue);
        }

    }

}
