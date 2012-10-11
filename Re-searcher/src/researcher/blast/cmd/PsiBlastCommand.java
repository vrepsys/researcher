package researcher.blast.cmd;

import java.util.ArrayList;
import java.util.List;

import researcher.utils.SeqUtil;

//-e Expectation value (E) (Expect)
//  /Statistinio reiksmingumo ivertis/
//  default = 10.0
//
//-W  Word size (Word Size)
//   /lango dydis: 2 arba 3/
//   default = 3
//
//-M Matrix (Matrix)
//  /amino rugsciu iverciu matrica: sarasas, leist pasirinkt/
//  default = BLOSUM62
//
//-G Cost to open a gap (Gap Costs)
//  /bauda pradedadnt tarpeliu serija palyginyje/
//  default = 11
//
//-E Cost to extend a gap (Gap Costs)
//  /tarpelio pletimo bauda/
//  default = 1
//
//-a Number of processors to use
//  /Si parametras turetu buti keiciamas tik administratoriaus;
//  kitiems vartotojams jo reiksme paliekama tokia, kokia nurode
//  administratorius, ir parametras neturetu but vaizduojamas
//  puslapyje/
//  default = 1
//
//-h  e-value threshold for inclusion in multipass model (with inclusion
//threshold)
//   /stat. reiksmingumo slenkstis itraukiant susijusias sekas sekancioje
//iteracijoje/
//   default = 0.002
//
//-j  Maximum number of passes to use in  multipass version
//   /iteraciju skaicius/
//   default = 3
//
//-v  Number of database sequences to show one-line descriptions for (V)
//(Descriptions)
//   /susijusiu seku anotaciju kiekis isvedime/
//   default = 500
//
//-b  Number of database sequence to show alignments for (B) (Alignments)
//   /maksimalus palyginiu kiekis isvedime/
//   default = 500
//
//-R  Input File for PSI-BLAST Restart [File In]
//   /pozicine iverciu matrica PSSM, kuria naudojantis bus tesiama palyginiu
//   paieska: vartotojui leist pasirinkt per Browse dialogini langa/
//   default = none
//
//-U  Use lower case filtering of FASTA sequence [T/F]
//   /naudoti mazuju raidziu filtracija: checkbox/
//   default = F

public class PsiBlastCommand {

	private Integer alignmentsToShow = null;

	private Integer descriptionsToShow = null;

	private Integer costToOpenAGap = null;

	private Integer costToExtendAGap = null;

	private String matrix = null;

	private Integer wordSize = null;

	private String sequence;

	private String pathToDatabase;

	private Integer numberOfProcessorsToUse = null;

	private Double evalue = null;

	private BlastOutputStyle outputStyle = null;

	private Integer numberOfIterations = null;

	private Double evalueMultipass = null;

	private String inputAlignmentFile = null;

	private String sequenceName = null;

	private String checkpointFileInput = null;

	private String checkpointFileOutput = null;

	private String otherCommandLineOptions = null;

	private String command = null;

	private String compositionBasedStatistics = null;

	private Boolean lowComplexity = null;

	private Boolean maskForLookup = null;

	public String getCompositionBasedStatistics() {
		return compositionBasedStatistics;
	}

	public void setCompositionBasedStatistics(String compositionBasedStatistics) {
		this.compositionBasedStatistics = compositionBasedStatistics;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public Double getEvalue() {
		return evalue;
	}

	public void setEvalue(Double evalue) {
		this.evalue = evalue;
	}

	public Integer getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(Integer numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public Integer getNumberOfProcessorsToUse() {
		return numberOfProcessorsToUse;
	}

	public void setNumberOfProcessorsToUse(Integer numberOfProcessorsToUse) {
		this.numberOfProcessorsToUse = numberOfProcessorsToUse;
	}

	public BlastOutputStyle getOutputStyle() {
		return outputStyle;
	}

	public void setOutputStyle(BlastOutputStyle outputStyle) {
		this.outputStyle = outputStyle;
	}

	public String getPathToDatabase() {
		return pathToDatabase;
	}

	public void setPathToDatabase(String pathToDatabase) {
		if (pathToDatabase == null)
			throw new NullPointerException(
					"parameter pathToDatabase can not be null");
		this.pathToDatabase = pathToDatabase;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		if (sequence == null)
			throw new NullPointerException("parameter sequence can not be null");
		this.sequence = sequence;
	}

	public PsiBlastCommand(String command, String sequenceName,
			String sequence, String pathToDatabase) {
		if (sequenceName == null)
			throw new NullPointerException(
					"parameter sequenceName can not be null");
		if (sequence == null)
			throw new NullPointerException("parameter sequence can not be null");
		if (pathToDatabase == null)
			throw new NullPointerException(
					"parameter pathToDatabase can not be null");

		this.sequence = sequence;
		this.sequenceName = sequenceName;
		this.pathToDatabase = pathToDatabase;
		this.command = command;
	}

	private static String booleanToString(boolean b) {
		if (b) {
			return "T";
		} else {
			return "F";
		}
	}

	@Override
	public String toString() {
		List<String> parameters = new ArrayList<String>();
		if (numberOfProcessorsToUse != null) {
			parameters.add("-a");
			parameters.add(numberOfProcessorsToUse.toString());
		}
		if (evalue != null) {
			parameters.add("-e");
			parameters.add(evalue.toString());
		}
		if (outputStyle != null) {
			parameters.add("-m");
			parameters.add(String.valueOf(outputStyle.getNumber()));
		}
		if (numberOfIterations != null) {
			parameters.add("-j");
			parameters.add(numberOfIterations.toString());
		}
		if (wordSize != null) {
			parameters.add("-W");
			parameters.add(wordSize.toString());
		}
		if (alignmentsToShow != null) {
			parameters.add("-b");
			parameters.add(alignmentsToShow.toString());
		}
		if (costToExtendAGap != null) {
			parameters.add("-E");
			parameters.add(costToExtendAGap.toString());
		}
		if (costToOpenAGap != null) {
			parameters.add("-G");
			parameters.add(costToOpenAGap.toString());
		}
		if (descriptionsToShow != null) {
			parameters.add("-v");
			parameters.add(descriptionsToShow.toString());
		}
		if (evalueMultipass != null) {
			parameters.add("-h");
			parameters.add(evalueMultipass.toString());
		}
		if (matrix != null) {
			parameters.add("-M");
			parameters.add(matrix.toString());
		}
		if (inputAlignmentFile != null) {
			parameters.add("-B");
			parameters.add(inputAlignmentFile);
		}
		if (checkpointFileOutput != null) {
			parameters.add("-C");
			parameters.add(checkpointFileOutput);
		}
		if (checkpointFileInput != null) {
			parameters.add("-R");
			parameters.add(checkpointFileInput);
		}
		if (compositionBasedStatistics != null) {
			parameters.add("-t");
			parameters.add(compositionBasedStatistics);
		}
//		1) kai pazymeta tik low complexity, turi buti -F 'T'
//		2) kai pazymetas tik mask for lookup table only, turi buti -F 'm S'
//		4) kai ne vienas is ju nera pazymetas, filtravimas nevykdomas, turi buti
//		-F 'F'
//		3) kai pazymeti abu ir low complexity ir mask for lookup table only.
//		Sitas atvejis yra savotiskas. Logikos jame nera, nes turetu buti du
//		nesuderinami dalykai. Taciau is tikruju web'inis NCBI BLAST variantas ta
//		leidzia. As tiesiog patikrinau, kas gaunasi, kai NCBI BLAST paieskoj
//		pazymejau abu. Pasirodo, rezultatas yra identiskas kaip ir pazymejus tik
//		"mask for lookup table only". Toj NCBI HTML formoj ir ta, ir ta opcija
//		priskiria reiksme tam paciam input name="FILTER", ir matyt tik
//		paskutinis (mask for lookup table only) priskyrimas galioja. Is esmes
//		galima palikti tik (1), (2) ir (4) galimybes, arba padaryti taip, kad
//		(3) vis vien suveiktu kaip (2).
		if (maskForLookup) {
			parameters.add("-F");
			parameters.add("'m S'");
		}		
		else if (lowComplexity) {
			parameters.add("-F");
			parameters.add("T");
		}
		else {
			parameters.add("-F");
			parameters.add("F");
		}
		parameters.add("-d");
		parameters.add(pathToDatabase);
		StringBuffer blastCmd = new StringBuffer();
		blastCmd.append(command);
		blastCmd.append(" ");
		for (String el : parameters) {
			blastCmd.append(el);
			blastCmd.append(" ");
		}
		if (otherCommandLineOptions != null) {
			blastCmd.append(" " + otherCommandLineOptions);
		}
		// String blastCmd = "blastpgp -j 1 -d /usr/data/blast/db/nr70 -e 2e-82
		// -a 2 -m 7";
		String cmd = "echo \"\n" + SeqUtil.getFasta(sequenceName, sequence)
				+ "\" | " + blastCmd;
		return cmd;
	}

	public Integer getAlignmentsToShow() {
		return alignmentsToShow;
	}

	public void setAlignmentsToShow(Integer alignmentsToShow) {
		this.alignmentsToShow = alignmentsToShow;
	}

	public Integer getCostToExtendAGap() {
		return costToExtendAGap;
	}

	public void setCostToExtendAGap(Integer costToExtendAGap) {
		this.costToExtendAGap = costToExtendAGap;
	}

	public Integer getCostToOpenAGap() {
		return costToOpenAGap;
	}

	public void setCostToOpenAGap(Integer costToOpenAGap) {
		this.costToOpenAGap = costToOpenAGap;
	}

	public Integer getDescriptionsToShow() {
		return descriptionsToShow;
	}

	public void setDescriptionsToShow(Integer descriptionsToShow) {
		this.descriptionsToShow = descriptionsToShow;
	}

	public Double getEvalueMultipass() {
		return evalueMultipass;
	}

	public void setEvalueMultipass(Double evalueMultipass) {
		this.evalueMultipass = evalueMultipass;
	}

	public String getMatrix() {
		return matrix;
	}

	public void setMatrix(String matrix) {
		this.matrix = matrix;
	}

	public Integer getWordSize() {
		return wordSize;
	}

	public void setWordSize(Integer wordSize) {
		this.wordSize = wordSize;
	}

	public String getInputAlignmentFile() {
		return inputAlignmentFile;
	}

	public void setInputAlignmentFile(String fileForRestart) {
		this.inputAlignmentFile = fileForRestart;
	}

	public String getCheckpointFileInput() {
		return checkpointFileInput;
	}

	public void setCheckpointFileInput(String checkpointFileInput) {
		this.checkpointFileInput = checkpointFileInput;
	}

	public String getCheckpointFileOutput() {
		return checkpointFileOutput;
	}

	public void setCheckpointFileOutput(String checkpointFileOutput) {
		this.checkpointFileOutput = checkpointFileOutput;
	}

	public String getOtherCommandLineOptions() {
		return otherCommandLineOptions;
	}

	public void setOtherCommandLineOptions(String otherCommandLineOptions) {
		this.otherCommandLineOptions = otherCommandLineOptions;
	}

	public Boolean getLowComplexity() {
		return lowComplexity;
	}

	public void setLowComplexity(Boolean lowComplexity) {
		this.lowComplexity = lowComplexity;
	}

	public Boolean getMaskForLookup() {
		return maskForLookup;
	}

	public void setMaskForLookup(Boolean maskForLookup) {
		this.maskForLookup = maskForLookup;
	}

}
