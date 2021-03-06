package org.jodaengine.process.structure.condition;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import org.jodaengine.exception.JodaEngineRuntimeException;
import org.jodaengine.process.structure.Condition;
import org.jodaengine.process.token.Token;
import org.jodaengine.util.juel.ProcessELContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.odysseus.el.ExpressionFactoryImpl;

/**
 * This {@link Condition} accepts JuelExpression. It means that this Condition is able to process a juelExpression
 */
public class JuelExpressionCondition implements Condition {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String juelExpression;

    /**
     * Creates a Juel-enabled condition.
     * 
     * @param juelExpression the juel string
     */
    public JuelExpressionCondition(String juelExpression) {

        this.juelExpression = juelExpression;
    }

    @Override
    public boolean evaluate(Token token) {

        ExpressionFactory factory = new ExpressionFactoryImpl();
        ELContext context = new ProcessELContext(token.getInstance().getContext(), false);
        
        ValueExpression e = factory.createValueExpression(context, juelExpression, boolean.class);
        
        try {
            return (Boolean) e.getValue(context);
            
        } catch (PropertyNotFoundException propertyNotFoundException) {
            String errorMessage = "The expression '" + juelExpression
                + "' contains a variable that could not be resolved properly. See message: "
                + propertyNotFoundException.getMessage();
            logger.error(errorMessage, propertyNotFoundException);
            throw new JodaEngineRuntimeException(errorMessage, propertyNotFoundException);
        }
    }
}
