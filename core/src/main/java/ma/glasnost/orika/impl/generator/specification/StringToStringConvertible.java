package ma.glasnost.orika.impl.generator.specification;

import static java.lang.String.format;
import static ma.glasnost.orika.impl.generator.SourceCodeContext.statement;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.generator.SourceCodeContext;
import ma.glasnost.orika.impl.generator.VariableRef;
import ma.glasnost.orika.metadata.FieldMap;
import ma.glasnost.orika.metadata.Property;

public class StringToStringConvertible extends AbstractSpecification {

    public StringToStringConvertible(MapperFactory mapperFactory) {
        super(mapperFactory);
    }

    public boolean appliesTo(FieldMap fieldMap) {
        return String.class.equals(fieldMap.getSource().getType().getRawType())
                && (fieldMap.getDestination().getType().isPrimitive() || fieldMap.getDestination().getType().isPrimitiveWrapper());
    }

    public String generateEqualityTestCode(VariableRef source, VariableRef destination, Property inverseProperty, SourceCodeContext code) {
        return "(" + source.notNull() + " && " + source + ".equals(\"\" + " + destination +"))";
    }

    public String generateMappingCode(VariableRef source, VariableRef destination, Property inverseProperty, SourceCodeContext code) {
        String value = source.toString();
        if (String.class.equals(source.rawType()) && (Character.class.equals(destination.rawType()) || char.class.equals(destination.rawType()))) {
            value = value + ".charAt(0)";
        }
        if (destination.isPrimitive()) {
            return statement(destination.assign("%s.valueOf(%s)", destination.wrapperTypeName(), value));
        } else {
            String mapNull = code.shouldMapNulls() ? format(" else { %s; }", destination.assignIfPossible("null")): "";
            return statement(format("%s {\n %s; } %s", source.ifNotNull(), destination.assign("%s.valueOf(%s)", destination.typeName(), value), mapNull));
        }
    }
}
