package in.zetasys.experimentor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.swing.DefaultListModel;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Range;
import weka.core.Utils;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.PairedTTester;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.ResultMatrix;
import weka.experiment.ResultMatrixPlainText;
import weka.experiment.SplitEvaluator;

@ManagedBean
public class ExperimentorController {

	private String expType;
	private String splitType;
	private String folds;
	private String percentage;
	private String runs;
	private ArrayList<String> dataSets;
	private String expResult;
	private String result;
	private String resultExt;
	private ArrayList<String> algos;
	private TreeNode allAlgos;
	private TreeNode submittedAlgo;
	private String selectedDataSet;
	private String submittedDataSet;
	private String selectedAlgo;

	public TreeNode getSubmittedAlgo() {
		return submittedAlgo;
	}

	public void setSubmittedAlgo(TreeNode submittedAlgo) {
		this.submittedAlgo = submittedAlgo;
	}

	public String getSubmittedDataSet() {
		return submittedDataSet;
	}

	public void setSubmittedDataSet(String submittedDataSet) {
		this.submittedDataSet = submittedDataSet;
	}

	public void setSelectedAlgo(String selectedAlgo) {
		this.selectedAlgo = selectedAlgo;
	}

	public ArrayList<String> getDataSets() {
		return dataSets;
	}

	public void setDataSets(ArrayList<String> dataSets) {
		this.dataSets = dataSets;
	}

	public ArrayList<String> getAlgos() {
		return algos;
	}

	public void setAlgos(ArrayList<String> algos) {
		this.algos = algos;
	}

	public String getSelectedAlgo() {
		return selectedAlgo;
	}

	@PostConstruct
	public void init() {
		allAlgos = new DefaultTreeNode("weka", null);
		TreeNode classifiers = new DefaultTreeNode("classifiers", allAlgos);
		TreeNode bayes = new DefaultTreeNode("bayes", classifiers);
		TreeNode bayesNet = new DefaultTreeNode("BayesNet", bayes);
		TreeNode naiveBayes = new DefaultTreeNode("NaiveBayes", bayes);
		TreeNode naiveBayesMultinomialText = new DefaultTreeNode("NaiveBayesMultinomialText", bayes);
		TreeNode functions = new DefaultTreeNode("functions", classifiers);
		TreeNode lazy = new DefaultTreeNode("lazy", classifiers);
		TreeNode meta = new DefaultTreeNode("meta", classifiers);
		TreeNode misc = new DefaultTreeNode("misc", classifiers);
		TreeNode rules = new DefaultTreeNode("rules", classifiers);
		TreeNode zeroR = new DefaultTreeNode("ZeroR", rules);
		TreeNode trees = new DefaultTreeNode("trees", classifiers);
		dataSets = new ArrayList<>();
		algos = new ArrayList<>();
	}

	public void addNewDs() {
		setSubmittedDataSet(null);
	}

	public void editDs() {
		setSubmittedDataSet(getSelectedDataSet());
	}

	public void deleteDs() {
		int i = 0;
		int index = -1;
		for (String ds : dataSets) {
			if (ds.equals(getSelectedDataSet())) {
				index = i;
				break;
			}
			i = i + 1;
		}
		if (index != -1)
			getDataSets().remove(index);
	}

	public void addNewAlgo() {
		setSubmittedAlgo(null);
	}

	public void editAlgo() {

	}

	public void deleteAlgo() {
		int i = 0;
		int index = -1;
		for (String algo : algos) {
			if (algo.equals(getSelectedAlgo())) {
				index = i;
				break;
			}
			i = i + 1;
		}
		if (index != -1)
			getAlgos().remove(index);
	}

	public TreeNode getAllAlgos() {

		return allAlgos;
	}

	public void setAllAlgos(TreeNode allAlgos) {
		this.allAlgos = allAlgos;
	}

	public String getSelectedDataSet() {
		return selectedDataSet;
	}

	public void setSelectedDataSet(String selectedDataSet) {
		this.selectedDataSet = selectedDataSet;
	}

	public void onAlgoNodeSelect() {

	}

	public void onAlgoNodeUnSelect() {
		setSubmittedAlgo(null);
	}

	public void submitNewDataSet() {
		int i = 0;
		int index = -1;
		boolean isSubmittedDataSetExisting = false;
		for (String ds : dataSets) {
			if ((getSubmittedDataSet() != null && ds.equals(getSubmittedDataSet()))
					|| (getSelectedDataSet() != null && ds.equals(getSelectedDataSet()))) {
				if (ds.equals(getSubmittedDataSet()))
					isSubmittedDataSetExisting = true;
				index = i;
				break;
			}
			i = i + 1;
		}
		if (index == -1)
			getDataSets().add(getSubmittedDataSet());
		else if (getSubmittedDataSet() != null && !getSubmittedDataSet().equals(getSelectedDataSet())
				&& !isSubmittedDataSetExisting)
			getDataSets().add(index, getSubmittedDataSet());

	}

	public void submitNewAlgo() {
		int i = 0;
		int index = -1;
		boolean isSubmittedAlgoExisting = false;
		for (String algo : algos) {
			if (getSubmittedAlgo() != null && algo.equals((String) getSubmittedAlgo().getData())
					|| getSelectedAlgo() != null && algo.equals(getSelectedAlgo())) {
				if (algo.equals((String) getSubmittedAlgo().getData()))
					isSubmittedAlgoExisting = true;
				index = i;
				break;
			}
			i = i + 1;
		}
		if (index == -1)
			getAlgos().add((String) getSubmittedAlgo().getData());
		else if (getSubmittedAlgo() != null && !((String) getSubmittedAlgo().getData()).equals(getSelectedAlgo())
				&& !isSubmittedAlgoExisting)

			getDataSets().add(index, (String) getSubmittedAlgo().getData());

	}

	public void onSelectDs(SelectEvent se) {
		setSelectedDataSet((String) se.getObject());
	}

	public void onUnselectDs(SelectEvent se) {
		setSelectedDataSet(null);
	}

	public void onReorderDs() {
	}

	public void onSelectAlgo(SelectEvent se) {
		setSelectedAlgo((String) se.getObject());
		
	}

	public void onUnselectAlgo(SelectEvent se) {
		setSelectedAlgo(null);
		
	}

	public void onReorderAlgo() {
	}

	public void runExperiment() {
		Experiment exp = new Experiment();
		exp.setPropertyArray(new Classifier[0]);
		exp.setUsePropertyIterator(true);
		if (getExpType().length() == 0)
			throw new IllegalArgumentException("No experiment type provided!");

		SplitEvaluator se = null;
		Classifier sec = null;
		boolean classification = false;
		String option = getExpType();
		if (option.equals("classification")) {
			classification = true;
			se = new ClassifierSplitEvaluator();
			sec = ((ClassifierSplitEvaluator) se).getClassifier();
		} else if (option.equals("regression")) {
			se = new RegressionSplitEvaluator();
			sec = ((RegressionSplitEvaluator) se).getClassifier();
		} else {
			throw new IllegalArgumentException("Unknown experiment type '" + option + "'!");
		}
		// crossvalidation or randomsplit
		option = getSplitType();
		if (option.length() == 0)
			throw new IllegalArgumentException("No split type provided!");

		if (option.equals("crossvalidation")) {
			CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
			option = getFolds();
			if (option.length() == 0)
				throw new IllegalArgumentException("No folds provided!");
			cvrp.setNumFolds(Integer.parseInt(option));
			cvrp.setSplitEvaluator(se);

			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se,
						new PropertyDescriptor("splitEvaluator", CrossValidationResultProducer.class),
						CrossValidationResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()),
						se.getClass());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			exp.setResultProducer(cvrp);
			exp.setPropertyPath(propertyPath);

		} else if (option.equals("randomsplit")) {
			RandomSplitResultProducer rsrp = new RandomSplitResultProducer();
			rsrp.setRandomizeData(true);
			option = getPercentage();
			if (option.length() == 0)
				throw new IllegalArgumentException("No percentage provided!");
			rsrp.setTrainPercent(Double.parseDouble(option));
			rsrp.setSplitEvaluator(se);

			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se,
						new PropertyDescriptor("splitEvaluator", RandomSplitResultProducer.class),
						RandomSplitResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()),
						se.getClass());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			exp.setResultProducer(rsrp);
			exp.setPropertyPath(propertyPath);
		} else {
			throw new IllegalArgumentException("Unknown split type '" + option + "'!");
		}

		// runs
		option = getRuns();
		if (option.length() == 0)
			throw new IllegalArgumentException("No runs provided!");
		exp.setRunLower(1);
		exp.setRunUpper(Integer.parseInt(option));

		// classifier
		Classifier[] cs = new Classifier[algos.size()];
		int i = 0;
		for (String algo : algos) {
			Classifier c = null;
			try {
				String[] options = null;
				String className = null;
				if (algo.equals("ZeroR")) {
					className = "weka.classifiers.rules.ZeroR";
					options = new String[] { "100", "False", "False", "2" };
				} else if (algo.equals("PART")) {
					className = "weka.classifiers.rules.PART";
					options = new String[] { "100", "False", "0.25", "False", "False", "False", "2", "2", "3", "False",
							"1", "False", "True" };
				}
				c = (Classifier) Utils.forName(Classifier.class, className, options);
				cs[i] = c;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i = i + 1;
		}
		exp.setPropertyArray(cs);
		// datasets
		boolean data = false;
		DefaultListModel model = new DefaultListModel();
		for (String ds : getDataSets()) {
			File file = new File(ds);
			if (!file.exists())
				throw new IllegalArgumentException("File '" + ds + "' does not exist!");
			data = true;
			model.addElement(file);
		}
		if (!data)
			throw new IllegalArgumentException("No data files provided!");
		exp.setDatasets(model);

		// result
		option = getResult();
		if (option.length() == 0)
			throw new IllegalArgumentException("No result file provided!");
		InstancesResultListener irl = new InstancesResultListener();
		irl.setOutputFile(new File(option + "." + getResultExt()));
		exp.setResultListener(irl);

		// 2. run experiment
		System.out.println("Initializing...");
		try {
			exp.initialize();
			System.out.println("Running...");
			exp.runExperiment();
			System.out.println("Finishing...");
			exp.postProcess();

			// 3. calculate statistics and output them
			System.out.println("Evaluating...");
			PairedTTester tester = new PairedCorrectedTTester();
			Instances result = new Instances(new BufferedReader(new FileReader(irl.getOutputFile())));
			tester.setInstances(result);
			tester.setSortColumn(-1);
			tester.setRunColumn(result.attribute("Key_Run").index());
			if (classification)
				tester.setFoldColumn(result.attribute("Key_Fold").index());
			tester.setDatasetKeyColumns(new Range("" + (result.attribute("Key_Dataset").index() + 1)));
			tester.setResultsetKeyColumns(new Range("" + (result.attribute("Key_Scheme").index() + 1) + ","
					+ (result.attribute("Key_Scheme_options").index() + 1) + ","
					+ (result.attribute("Key_Scheme_version_ID").index() + 1)));
			tester.setResultMatrix(new ResultMatrixPlainText());
			tester.setDisplayedResultsets(null);
			tester.setSignificanceLevel(0.05);
			tester.setShowStdDevs(true);
			// fill result matrix (but discarding the output)
			if (classification)
				tester.multiResultsetFull(0, result.attribute("Percent_correct").index());
			else
				tester.multiResultsetFull(0, result.attribute("Correlation_coefficient").index());
			// output results for reach dataset
			System.out.println("\nResult:");
			ResultMatrix matrix = tester.getResultMatrix();
			for (int j = 0; j < matrix.getColCount(); j++) {
				expResult = expResult + matrix.getColName(j) + System.getProperty("line.separator");
				expResult = expResult + "    Perc. correct: " + matrix.getMean(j, 0)
						+ System.getProperty("line.separator");
				expResult = expResult + "    StdDev: " + matrix.getStdDev(j, 0) + System.getProperty("line.separator");
				System.out.println(matrix.getColName(j));
				System.out.println("    Perc. correct: " + matrix.getMean(j, 0));
				System.out.println("    StdDev: " + matrix.getStdDev(j, 0));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public String getSplitType() {
		return splitType;
	}

	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}

	public String getFolds() {
		return folds;
	}

	public void setFolds(String folds) {
		this.folds = folds;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getRuns() {
		return runs;
	}

	public void setRuns(String runs) {
		this.runs = runs;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultExt() {
		return resultExt;
	}

	public void setResultExt(String resultExt) {
		this.resultExt = resultExt;
	}

	public String getExpResult() {
		return expResult;
	}

	public void setExpResult(String expResult) {
		this.expResult = expResult;
	}
}
