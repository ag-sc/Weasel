import datasetEvaluator.DatasetEvaluatorSandbox;


public class EntityLinking {

	public EntityLinking() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse();

		DatasetEvaluatorSandbox.evaluate();
	}

}
