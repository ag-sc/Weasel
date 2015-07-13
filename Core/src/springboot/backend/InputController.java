package springboot.backend;

import java.io.IOException;
import java.io.StringWriter;

import nif.ModelAdapter;
import nif.NIFAdapter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;

import datasetEvaluator.DatasetEvaluatorSandbox;
import datatypes.configuration.Config;
import entityLinker.InputStringHandler;

@RestController
public class InputController {

	DatasetEvaluatorSandbox sandbox;
	InputStringHandler handler;
	ModelAdapter adapter;

	public InputController() throws ClassCastException, ClassNotFoundException, IOException {
		System.out.println("Inputcontroller created");
		sandbox = new DatasetEvaluatorSandbox();
		adapter = new NIFAdapter(sandbox);
		handler = new InputStringHandler();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/veasel-english")
	public String handleInputEnglish(@RequestParam(value = "input") String input) {
		// error handling
		if (input == null)
			return "Empty input!";

		handler.resetModel();
		handler.handleString(input);
		Model model = handler.getModel();
		adapter.linkModel(model);
		StringWriter stringWriter = new StringWriter();
		model.write(stringWriter, "Turtle");
		String output = stringWriter.toString();

		return output;
	}
}
