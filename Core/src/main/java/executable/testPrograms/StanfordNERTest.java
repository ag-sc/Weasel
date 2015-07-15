package executable.testPrograms;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordNERTest {

	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		String serializedClassifier = "E:/Master Project/data/stanford models/english.all.3class.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		List<List<CoreLabel>> out = classifier.classify("Tiger was lost in the woods.");
		for (List<CoreLabel> sentence : out) {
			for (CoreLabel word : sentence) {
				System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
			}
			System.out.println();
		}
	}

}
