package org.brainstorm.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Cousineau
 */
public class BstNodeFilter {
    List<Fields> m_fields;
    List<Equality> m_equalities;
    List<String[]> m_values;
    List<BoolOperation> m_boolOperations;
    List<Boolean> m_caseSensitive;
    
    enum Fields {
        COMMENT,
        TYPE,
        FILENAME
    }
    enum Equality {
        CONTAINS,
        EQUALS
    }
    enum BoolOperation {
        AND,
        OR
    }
    
    // Initialize with no filters
    public BstNodeFilter() {
        m_fields = new ArrayList<Fields>();
        m_equalities = new ArrayList<Equality>();
        m_values = new ArrayList<String[]>();
        m_boolOperations = new ArrayList<BoolOperation>();
        m_caseSensitive = new ArrayList<Boolean>();
    }
    
    // Add a filter
    public void addFilter(String value) {
        addFilter(value, 1, 1, 1, false);
    }
    public void addFilter(String value, int type, int equality, int boolOperation, boolean caseSensitive) {
        addFilter(new String[] {value}, type, equality, boolOperation, caseSensitive);
    }
    public void addFilter(String[] values, int type, int equality, int boolOperation, boolean caseSensitive) {
        switch (type) {
            case 1:
                m_fields.add(Fields.COMMENT);
                break;
            case 2:
                m_fields.add(Fields.TYPE);
                break;
            case 3:
                m_fields.add(Fields.FILENAME);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type");
        }
        
        switch (equality) {
            case 1:
                m_equalities.add(Equality.CONTAINS);
                break;
            case 2:
                m_equalities.add(Equality.EQUALS);
                break;
            default:
                throw new IllegalArgumentException("Unsupported equality");
        }
        
        m_values.add(values);
        
        switch (boolOperation) {
            case 1:
                m_boolOperations.add(BoolOperation.AND);
                break;
            case 2:
                m_boolOperations.add(BoolOperation.OR);
                break;
            default:
                throw new IllegalArgumentException("Unsupported boolean operation");
        }
        
        m_caseSensitive.add(caseSensitive);
    }
    
    boolean matchesFilter(BstNode node) {
        boolean matchesCurVal, matchesAnyVal, matchesAll;
        String valueFilter, valueNode;
        String[] valuesFilter;
        
        // If no filter, it matches
        matchesAll = true;
        
        for (int iFilter = 0; iFilter < m_values.size(); iFilter++) {
            // Get appropriate value from node
            switch (m_fields.get(iFilter)) {
                case COMMENT:
                    valueNode = node.getComment();
                break;
                case TYPE:
                    valueNode = node.getType();
                    break;
                case FILENAME:
                    valueNode = node.getFileName();
                    break;
                default:
                    valueNode = "";
            }
            
            // Apply for each possible value
            valuesFilter = m_values.get(iFilter);
            matchesAnyVal = false;
            for (int iVal = 0; iVal < valuesFilter.length; iVal++) {
                valueFilter = valuesFilter[iVal];
                
                // Remove case if not case-sensitive
                if (!m_caseSensitive.get(iFilter)) {
                    valueFilter = valueFilter.toLowerCase();
                    if (iVal == 0)
                        valueNode = valueNode.toLowerCase();
                }
            
                // Test equality
                switch (m_equalities.get(iFilter)) {
                    case CONTAINS:
                        matchesCurVal = valueNode.contains(valueFilter);
                        break;
                    case EQUALS:
                        matchesCurVal = valueNode.equals(valueFilter);
                        break;
                    default:
                        matchesCurVal = true;
                }
                
                matchesAnyVal |= matchesCurVal;
            }
            
            // Apply boolean operation if not first filter
            if (iFilter == 0) {
                matchesAll = matchesAnyVal;
            } else {
                switch (m_boolOperations.get(iFilter)) {
                    case AND:
                        matchesAll &= matchesAnyVal;
                        break;
                    case OR:
                        matchesAll |= matchesAnyVal;
                        break;
                }
            }
        }
        
        return matchesAll;
    }
}
