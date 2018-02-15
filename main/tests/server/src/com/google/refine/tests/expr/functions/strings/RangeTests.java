package com.google.refine.tests.expr.functions.strings;

import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.ControlFunctionRegistry;
import com.google.refine.grel.Function;
import com.google.refine.tests.RefineTest;


/**
 * Tests for the range function.
 */
public class RangeTests extends RefineTest {

    static Properties bindings;

    @Override
    @BeforeTest
    public void init() {
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @BeforeMethod
    public void SetUp() {
        bindings = new Properties();
    }

    @AfterMethod
    public void TearDown() {
        bindings = null;
    }
    
    /**
     * Lookup a control function by name and invoke it with a variable number of args
     */
    private static Object invoke(String name,Object... args) {
        // registry uses static initializer, so no need to set it up
        Function function = ControlFunctionRegistry.getFunction(name);
        if (function == null) {
            throw new IllegalArgumentException("Unknown function " + name);
        }
        if (args == null) {
            return function.call(bindings, new Object[0]);
        } else {
            return function.call(bindings, args);
        }
    }
    
    @Test
    public void testRangeInvalidParams() {        
        // Test number of arguments
        Assert.assertTrue(invoke("range") instanceof EvalError);
        Assert.assertTrue(invoke("range", "") instanceof EvalError);
        Assert.assertTrue(invoke("range", "one","two","three") instanceof EvalError);
        Assert.assertTrue(invoke("range", 1, 2, 3, 4) instanceof EvalError);

        // Test invalid single string argument types
        Assert.assertTrue(invoke("range", "null") instanceof EvalError);
        Assert.assertTrue(invoke("range", "a") instanceof EvalError);

        // Test invalid single string numeric arguments
        Assert.assertTrue(invoke("range", "-1") instanceof EvalError);
        Assert.assertTrue(invoke("range", "1,") instanceof EvalError);
        Assert.assertTrue(invoke("range", ",") instanceof EvalError);
        Assert.assertTrue(invoke("range", ",2") instanceof EvalError);
        Assert.assertTrue(invoke("range", "1.5") instanceof EvalError);
        Assert.assertTrue(invoke("range", ",12.3, 2") instanceof EvalError);
        
        // Test invalid numeric arguments
        Assert.assertTrue(invoke("range", 1.2) instanceof EvalError);
        Assert.assertTrue(invoke("range", 1.2, 4.5) instanceof EvalError);
        Assert.assertTrue(invoke("range", 1.2, 5, 3) instanceof EvalError);
        Assert.assertTrue(invoke("range", -1) instanceof EvalError);
        Assert.assertTrue(invoke("range", -1, 2) instanceof EvalError);
        Assert.assertTrue(invoke("range", -1, 5, 3) instanceof EvalError);
        
        // Test invalid mixed arguments
        Assert.assertTrue(invoke("range", 1, "2, 3") instanceof EvalError);
        Assert.assertTrue(invoke("range", 1, "") instanceof EvalError);
    }
    
    @Test
    public void testRangeValidStringParams() {
        // Test valid single string argument
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "3"))), "0, 1, 2, 3");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", " 3  "))), "0, 1, 2, 3");
        
        // Test valid double string arguments
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 1"))), "1");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 5"))), "1, 2, 3, 4, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "5, 1"))), "5, 4, 3, 2, 1");
        
        // Test valid triple string arguments
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 3, 0"))), "");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 1, 1"))), "1");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 5, 10"))), "1");
        
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 5, 2"))), "1, 3, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "5, 1, 2"))), "5, 3, 1");
    }
    
    @Test
    public void testRangeValidIntegerParams() {
        // Test valid single integer argument
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 0))), "0");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 5))), "0, 1, 2, 3, 4, 5");
        
        // Test valid double integer arguments
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 1, 5))), "1, 2, 3, 4, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 5, 1))), "5, 4, 3, 2, 1");

        // Test valid triple integer arguments
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 1, 5, 0))), "");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 1, 5, 2))), "1, 3, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 5, 1, 2))), "5, 3, 1");
    }
    
    @Test
    public void testRangeValidMixedParams() {
        // Test when each argument is of type integer/ string
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1", 5))), "1, 2, 3, 4, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "5", 1))), "5, 4, 3, 2, 1");
        
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 1, "5"))), "1, 2, 3, 4, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", 5, "1"))), "5, 4, 3, 2, 1");
        
        // Test when step is an integer (e.g. range(value, 1))
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 3", 0))), "");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 1", 1))), "1");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 5", 10))), "1");
        
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "1, 5", 2))), "1, 3, 5");
        Assert.assertEquals(String.join(", ", (String[]) (invoke("range", "5, 1", 2))), "5, 3, 1");   
    }
}