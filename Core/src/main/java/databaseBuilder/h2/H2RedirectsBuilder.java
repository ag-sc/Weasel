package main.java.databaseBuilder.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import main.java.datatypes.StringEncoder;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

/**
 * @author Felix Tristram
 * Save to the database which entries are redirects and which are disambiguations.
 */
public class H2RedirectsBuilder extends H2BuilderCore {

	WikiXMLParser wxsp;
	static int abstractCounter = 0;
	static Connection connection;
	final String redirectUpdateQuery = "UPDATE EntityID SET redirectTo = (SELECT id FROM EntityID WHERE entity = (?)) WHERE entity = (?)";
	final String disambiguationUpdateQuery = "UPDATE EntityID SET isdisambiguation = true WHERE entity = (?)";

	public H2RedirectsBuilder(String dbPath, String wikipediaDumpPath, String username, String password) throws IOException {
		super(dbPath, username, password);
		wxsp = WikiXMLParserFactory.getSAXParser(wikipediaDumpPath);
	}

	public void run() throws Exception {
		long timeStart = System.nanoTime();

		Class.forName("org.h2.Driver");
		connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);

		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				public void process(WikiPage page) {
					abstractCounter++;
					if (abstractCounter % 1000000 == 0)
						System.out.println("Processed abstracts: " + abstractCounter);
					// if(page.getTitle().trim().equals("Reuters Television"))
					// System.out.println("Reuters Television");
					try {
						if (page.isRedirect()) {
							// System.out.println("Is redirect: " +
							// page.getTitle().trim() + " -> " +
							// page.getRedirectPage());

							String redirect = StringEncoder.encodeString(page.getTitle());
							String target = StringEncoder.encodeString(page.getRedirectPage());

							if (redirect != null && target != null) {
								preparedStatement = connection.prepareStatement(redirectUpdateQuery);
								preparedStatement.setString(1, target);
								preparedStatement.setString(2, redirect);
								preparedStatement.execute();
							}
						} else if (page.isDisambiguationPage()) {
							String disambiguation = StringEncoder.encodeString(page.getTitle());

							if (disambiguation != null) {
								preparedStatement = connection.prepareStatement(disambiguationUpdateQuery);
								preparedStatement.setString(1, disambiguation);
								preparedStatement.execute();
							}
						}
					} catch (Exception e) {
						System.out.println("Exception in SQL update operations: ");
						e.printStackTrace();
					}
				}
			});

			wxsp.parse();
		} catch (Exception e) {
			System.out.println("Exception in Parse operations: ");
			e.printStackTrace();
		}

		connection.commit();
		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
