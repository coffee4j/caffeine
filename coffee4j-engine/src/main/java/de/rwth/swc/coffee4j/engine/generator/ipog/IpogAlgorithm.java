package de.rwth.swc.coffee4j.engine.generator.ipog;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.util.ArrayUtil;
import de.rwth.swc.coffee4j.engine.util.Combinator;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.rwth.swc.coffee4j.engine.util.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.engine.util.CombinationUtil.containsAllParameters;

/**
 * An implementation of the popular IPOG algorithm. For given configuration it generates a test suite so that for each
 * t-value-combination there is a test input containing it. This means IPOG create a t-way-testing suite.
 * Some improvements from "An Efficient Design and Implementation of the In-Parameter-Order Algorithm" were used.
 * <p>
 * The algorithm was extended to offer support for constraints, dynamic parameter orders, and variable strength testing.
 * To introduce parameter orders, the strategy pattern is used with {@link ParameterOrder}, and the same is done for
 * variable strength testing via {@link ParameterCombinationFactory}.
 */
public class IpogAlgorithm {
    
    private final IpogConfiguration configuration;
    
    /**
     * Creates a new algorithm for the given configuration. After this, the {@link IpogAlgorithm#generate()} method can be used
     * to generate the test suite satisfying the configuration.
     *
     * @param configuration information about what test suite should be generated by IPOG. Must not be {@code null}
     * @throws NullPointerException if configuration is {@code null}
     */
    public IpogAlgorithm(IpogConfiguration configuration) {
        this.configuration = Preconditions.notNull(configuration);
    }
    
    public List<int[]> generate() {
        Int2IntMap parameters = convertToFactors(configuration.getTestModel());
        
        final int[] initialParameters = configuration.getOrder().getInitialParameters(parameters, configuration.getTestModel().getStrength());
        final List<int[]> testSuite = buildInitialTestSuite(parameters, initialParameters);
        final int[] remainingParameters = configuration.getOrder().getRemainingParameters(parameters, configuration.getTestModel().getStrength());

        if(configuration.getTestModel().getStrength() > 0) {
            extendInitialTestSuite(parameters, initialParameters, testSuite, remainingParameters);
        }

        fillEmptyValues(testSuite, parameters);
        
        return testSuite;
    }

    private void extendInitialTestSuite(Int2IntMap parameters, int[] initialParameters, List<int[]> testSuite, int[] remainingParameters) {
        final IntList coveredParameters = new IntArrayList(initialParameters);

        for (int i : remainingParameters) {
            List<IntSet> parameterCombinations = configuration.getFactory().create(coveredParameters.toIntArray(), configuration.getTestModel().getStrength());
            CoverageMap coverageMap = horizontalExtension(i, testSuite, parameters, parameterCombinations);

            if (coverageMap.hasUncoveredCombinations()) {
                verticalExtension(i, parameters, testSuite, coverageMap);
            }

            coveredParameters.add(i);
        }
    }

    private List<int[]> buildInitialTestSuite(Int2IntMap allParameters, int[] initialParameters) {
        List<int[]> testSuite = Combinator.computeCartesianProduct(subMap(allParameters, initialParameters), allParameters.size());
        
        return testSuite.stream().filter(configuration.getChecker()::isValid).collect(Collectors.toList());
    }
    
    private Int2IntMap convertToFactors(TestModel testModel) {
        Int2IntMap parameters = new Int2IntOpenHashMap(testModel.getNumberOfParameters());
        for (int i = 0; i < testModel.getNumberOfParameters(); i++) {
            parameters.put(i, testModel.getSizeOfParameter(i));
        }
        return parameters;
    }
    
    private Int2IntMap subMap(Int2IntMap original, int[] keys) {
        Int2IntMap subMap = new Int2IntOpenHashMap();
        
        for (int i = 0; i < original.size(); i++) {
            
            if (ArrayUtil.contains(keys, i)) {
                subMap.put(i, original.get(i));
            }
        }
        
        return subMap;
    }
    
    private CoverageMap horizontalExtension(int nextParameter, List<int[]> testSuite, Int2IntMap allParameters, List<IntSet> parameterCombinations) {
        CoverageMap coverageMap = constructCoverageMap(nextParameter, allParameters, parameterCombinations);
        
        for (int[] testInput : testSuite) {
            addValueWithHighestCoverageGain(coverageMap, testInput, nextParameter);
            coverageMap.markAsCovered(testInput);
            if (!coverageMap.hasUncoveredCombinations()) {
                break;
            }
        }
        
        return coverageMap;
    }
    
    private CoverageMap constructCoverageMap(int nextParameter, Int2IntMap allParameters, List<IntSet> parameterCombinations) {
        return new CoverageMap(parameterCombinations, nextParameter, allParameters, configuration.getChecker());
    }
    
    private void addValueWithHighestCoverageGain(CoverageMap coverageMap, int[] partialTestInput, int parameterIndex) {
        int[] gains = coverageMap.computeGainsOfFixedParameter(partialTestInput);
        
        for (int i = 0; i < gains.length; i++) {
            int valueWithHighestGain = getValueWithHighestGain(gains);
            
            int highestGain = gains[valueWithHighestGain];
            if (highestGain == -1) {
                // If you reach this branch, there's a programming error somewhere else"
                throw new IllegalStateException("ERROR: test input " + Arrays.toString(partialTestInput) + " cannot be updated for parameter " + parameterIndex);
            }
            
            partialTestInput[parameterIndex] = valueWithHighestGain;
            
            if (configuration.getChecker().isValid(partialTestInput)) {
                return;
            } else {
                partialTestInput[parameterIndex] = -1;
                gains[valueWithHighestGain] = -1;
            }
        }
        
        // If you reach this branch, there's a programming error somewhere else"
        throw new IllegalStateException("ERROR: test input " + Arrays.toString(partialTestInput) + " cannot be updated for parameter " + parameterIndex);
    }
    
    private int getValueWithHighestGain(int[] gains) {
        int valueWithHighestGain = 0;
        for (int value = 0; value < gains.length; value++) {
            if (gains[value] > gains[valueWithHighestGain]) {
                valueWithHighestGain = value;
            }
        }
        return valueWithHighestGain;
    }
    
    private void verticalExtension(int index, Int2IntMap parameters, List<int[]> testSuite, CoverageMap coverageMap) {
        CombinationPartitioner combinationPartitioner = new CombinationPartitioner(getIncompleteCombinations(index, testSuite), index, parameters.get(index));
        Optional<int[]> uncoveredCombination;
        
        while ((uncoveredCombination = coverageMap.getUncoveredCombination()).isPresent()) {
            int[] combination = uncoveredCombination.get();
            
            Optional<int[]> candidate = addCombinationToTestInput(combination, combinationPartitioner, testSuite);
            
            if (candidate.isPresent()) {
                int[] extension = candidate.get();
                
                coverageMap.markAsCovered(extension);
                
                if (containsAllParameters(extension, index)) {
                    combinationPartitioner.removeCombination(extension);
                }
            } else {
                coverageMap.markAsCovered(combination);
            }
        }
    }
    
    private List<int[]> getIncompleteCombinations(int index, List<int[]> testSuite) {
        List<int[]> incompleteCombinations = new LinkedList<>();
        for (int[] testInput : testSuite) {
            if (!containsAllParameters(testInput, index)) {
                incompleteCombinations.add(testInput);
            }
        }
        return incompleteCombinations;
    }
    
    private Optional<int[]> addCombinationToTestInput(int[] combination, CombinationPartitioner combinationPartitioner, List<int[]> testSuite) {
        if (!configuration.getChecker().isValid(combination)) {
            return Optional.empty();
        }
        
        Optional<int[]> testInput = combinationPartitioner.extendSuitableCombination(combination, configuration.getChecker());
        
        if (testInput.isPresent()) {
            return testInput;
        } else {
            testSuite.add(combination);
            combinationPartitioner.addCombination(combination);
            
            return Optional.of(combination);
        }
    }
    
    private void fillEmptyValues(List<int[]> testSuite, Int2IntMap parameters) {
        for (int[] testInput : testSuite) {
            for (int parameter = 0; parameter < parameters.size(); parameter++) {
                if (testInput[parameter] == NO_VALUE) {
                    fillEmptyValue(testInput, parameter, parameters.get(parameter));
                }
            }
        }
    }
    
    private void fillEmptyValue(int[] testInput, int parameter, int parameterSize) {
        for (int value = 0; value < parameterSize; value++) {
            if (configuration.getChecker().isExtensionValid(testInput, parameter, value)) {
                testInput[parameter] = value;
                return;
            }
        }
        
        // If you reach this branch, there's a programming error somewhere else"
        throw new IllegalStateException("ERROR: could not replace random value for parameter " + parameter + " in test input: " + Arrays.toString(testInput));
    }
}
