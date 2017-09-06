

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.services.util.XmlFormatter;
import org.hucompute.textimager.uima.polyglot.PolyglotMorphology;
import org.hucompute.textimager.uima.polyglot.PolyglotLanguage;
import org.hucompute.textimager.uima.polyglot.PolyglotNamedEntity;
import org.hucompute.textimager.uima.polyglot.PolyglotPartOfSpeech;
import org.hucompute.textimager.uima.polyglot.PolyglotSentiment;
import org.hucompute.textimager.uima.polyglot.PolyglotSentenceBoundary;
import org.hucompute.textimager.uima.polyglot.PolyglotTokenizer;
import org.hucompute.textimager.uima.polyglot.PolyglotTransliteration;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* Pipeline
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* Central class for the analyzation of text.
*
*/
public class Pipeline {
	
	public static void main(String[] args) throws UIMAException, CASRuntimeException, IOException {		
		// String text = "Türkiye ya da resmî adıyla Türkiye Cumhuriyeti, topraklarının büyük bölümü Anadolu'ya, küçük bir bölümü ise Balkanlar'ın uzantısı olan Trakya'ya yayılmış bir ülke. Vikipedi'nin güvenilebilirliği ve doğruluğu üzerine tartışmalar mevcuttur ve site yoğun olarak vandalizme maruz kalmaktadır.";	
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription posAnnotator = createEngineDescription(PolyglotPartOfSpeech.class, PolyglotPartOfSpeech.PARAM_PYTHON_PATH, "/usr/bin/python", PolyglotPartOfSpeech.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/pos-default.map");
		
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(FileUtils.readFileToString(new File("src/main/resources/input.txt")));
		//inputCas.setDocumentText("We will meet at eight o'clock on Thursday morning.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, posAnnotator);
		
		// Output as XML
		String output = XmlFormatter.getPrettyString(inputCas.getCas());
		System.out.println(output);
		
		// Ausgabe in eine Datei schreiben.
		File file = new File("Output.xml");
		try {
			FileUtils.writeStringToFile(file, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
