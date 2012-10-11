package researcher.web;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tapestry.form.TextField;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;
import org.hibernate.Hibernate;

import researcher.beans.BlastDatabase;
import researcher.beans.BlastQuery;
import researcher.beans.SecondaryBlastQuery;
import researcher.blast.CompositionalAdjustments;
import researcher.blast.SearchLocation;
import researcher.cache.Cache;
import researcher.utils.SeqUtil;
import researcher.utils.Utils;

public abstract class NewSequence extends ResearcherPage {

	@Bean
	public abstract ValidationDelegate getValidation();

	@Component(id = "sequenceName", type = "TextField", bindings = {
			"value=sequenceName", "displayName=message:sequenceName" })
	abstract public TextField getSequenceNameField();

	abstract public String getSequenceName();

	abstract public void setSequenceName(String seqName);

	abstract public String getSequenceString();

	abstract public void setSequenceString(String seqString);

	@InitialValue("literal:0.002")
	abstract public double getEValue();

	abstract public void setEValue(double eval);

	@InitialValue("literal:0.002")
	abstract public double getEValue2();

	abstract public void setEValue2(double eval);

	@InitialValue("literal:0.002")
	abstract public double getEValueMultipass();

	abstract public void setEValueMultipass(double evalMultipass);

	@InitialValue("literal:3")
	abstract public int getNumberOfIterations();

	abstract public void setNumberOfIterations(int iterations);

	@InitialValue("literal:7")
	abstract public int getPeriod();

	abstract public void setPeriod(int period);

	@InitialValue("literal:3")
	abstract public int getWordSize();

	abstract public void setWordSize(int wordSize);

	@InitialValue("literal:3")
	abstract public int getWordSize2();

	abstract public void setWordSize2(int wordSize);

	abstract public String getMatrix();

	abstract public void setMatrix(String matrix);

	abstract public String getMatrix2();

	abstract public void setMatrix2(String matrix);

	@InitialValue("literal:11")
	abstract public int getCostToOpenAGap();

	abstract public void setCostToOpenAGap(int costOpen);

	@InitialValue("literal:11")
	abstract public int getCostToOpenAGap2();

	abstract public void setCostToOpenAGap2(int costOpen);

	@InitialValue("literal:1")
	abstract public int getCostToExtendAGap();

	abstract public void setCostToExtendAGap(int costExtend);

	@InitialValue("literal:1")
	abstract public int getCostToExtendAGap2();

	abstract public void setCostToExtendAGap2(int costExtend);

	abstract public IUploadFile getRestartFile();

	@Persist("client")
	abstract public String getRestartFileName();

	abstract public void setRestartFileName(String fname);

	@Persist
	abstract public Blob getRestartFileBlob();

	abstract public void setRestartFileBlob(Blob rsfile);

	abstract public BlastDatabase getDatabase();

	abstract public void setDatabase(BlastDatabase db);

	abstract public BlastDatabase getNcbiDatabase();

	abstract public void setNcbiDatabase(BlastDatabase db);

	abstract public BlastDatabase getDatabase2();

	abstract public void setDatabase2(BlastDatabase db);

	abstract public BlastDatabase getNcbiDatabase2();

	abstract public void setNcbiDatabase2(BlastDatabase db);

	abstract public String getCommandLineOptions();

	abstract public void setCommandLineOptions(String cmdOpts);

	abstract public String getCommandLineOptions2();

	abstract public void setCommandLineOptions2(String cmdOpts);

	abstract public String getInfoMessage();

	abstract public void setInfoMessage(String infoMsg);

	abstract public SearchLocation getSearchLocation();

	abstract public void setSearchLocation(SearchLocation searchLocation);

	abstract public String getPssm();

	abstract public void setPssm(String pssm);

	abstract public String getPhiPattern();

	abstract public void setPhiPattern(String pp);

	abstract public Boolean getNotifyByMail();

	abstract public void setNotifyByMail(Boolean notify);

	abstract public CompositionalAdjustments getCompositionalAdjustments();

	abstract public void setCompositionalAdjustments(CompositionalAdjustments c);

	abstract public CompositionalAdjustments getCompositionalAdjustments2();

	abstract public void setCompositionalAdjustments2(CompositionalAdjustments c);

	abstract public boolean getLowComplexity();

	abstract public void setLowComplexity(boolean c);

	abstract public boolean getLowComplexity2();

	abstract public void setLowComplexity2(boolean c);

	abstract public boolean getMaskLookup();

	abstract public void setMaskLookup(boolean c);

	abstract public boolean getMaskLookup2();

	abstract public void setMaskLookup2(boolean c);

	abstract public Integer getMaxNumberOfHits();

	abstract public void setMaxNumberOfHits(Integer c);

	abstract public boolean getNeedRefreshMaxHits();

	abstract public void setNeedRefreshMaxHits(boolean cfe);

	public boolean localSearchReady() {
		if (Cache.getConfig() != null && Cache.getConfig().localServerIsReady())
			return true;
		return false;
	}

	public boolean mailConfigured() {
		if (Cache.getConfig() != null && Cache.getConfig().emailConfigured())
			return true;
		return false;
	}

	@Override
	public void pageBeginRender(PageEvent event) {
		if (getSearchLocation() == null) {
			setSearchLocation(SearchLocation.NCBI);
			setMaxNumberOfHits(1000);
		}
		if (getCompositionalAdjustments() == null) {
			setCompositionalAdjustments(CompositionalAdjustments.COMPOSITION_BASED_STATISTICS);
			setCompositionalAdjustments2(CompositionalAdjustments.COMPOSITION_BASED_STATISTICS);
		}

		if (getSearchLocation() == SearchLocation.NCBI) {
		
			if (getNcbiDatabase() == null) {
				setDatabase(Cache.getNcbiDatabases().get(3));
			}
			
		} else if (getSearchLocation() == SearchLocation.LOCAL_SERVER) {
			
			if (Cache.getLocalPsiblastDatabases().size() > 0
					&& getDatabase() == null) {
				setDatabase(Cache.getLocalPsiblastDatabases().get(0));
			}
			
			
		}

		if (getNotifyByMail() == null) {
			setNotifyByMail(true);
		}

		setNeedRefreshMaxHits(false);

		super.pageBeginRender(event);
	}

	public void onChangeSearchLocation() {

	}

	public static IPropertySelectionModel getWordSizeItems() {
		return new StringPropertySelectionModel(new String[] { "3", "2" });
	}

	public static IPropertySelectionModel getMatrixItems() {
		return new StringPropertySelectionModel(new String[] { "BLOSUM62",
				"BLOSUM80", "BLOSUM45", "PAM30", "PAM70" });
	}

	public IPropertySelectionModel getDatabaseItems() {
		return new SelectionModels.DatabaseSelectionModel(Cache
				.getLocalPsiblastDatabases());
	}

	public IPropertySelectionModel getNcbiDatabaseItems() {
		return new SelectionModels.DatabaseSelectionModel(Cache
				.getNcbiDatabases());
	}

	public IPropertySelectionModel getSecondaryDatabaseItems() {
		return new SelectionModels.DatabaseSelectionModel(Cache
				.getLocalPsiblastDatabases(), getMessages().getMessage(
				"noSecondarySearch"));
	}

	public IPropertySelectionModel getNcbiSecondaryDatabaseItems() {
		return new SelectionModels.DatabaseSelectionModel(Cache
				.getNcbiDatabases(), getMessages().getMessage(
				"noSecondarySearch"));
	}

	public IPropertySelectionModel getSearchLocationItems() {
		return new SelectionModels.SearchLocationSelectionModel();
	}

	public IPropertySelectionModel getCompositionalAdjustmentsItems() {
		return new SelectionModels.CompositionalAdjustmentsModel();
	}

	public ILink onRemoveRestartFile() {
		setRestartFileBlob(null);
		setRestartFileName(null);
		return null;
	}
	
	public void onFormRefresh() {
		if (getSearchLocation() == SearchLocation.NCBI) {
			setMaxNumberOfHits(1000);
		}
		else if (getSearchLocation() == SearchLocation.LOCAL_SERVER) {
			setMaxNumberOfHits(10000);
		}
	}

	public ILink onOk() {
		onAdd();
		if (getValidation().getHasErrors())
			return null;
		return getPageService().getLink(false, "SequenceList");
	}

	public ILink onAdd() {

		BlastDatabase database;
		BlastDatabase secondaryDatabase;
		if (getSearchLocation() == SearchLocation.LOCAL_SERVER) {
			database = getDatabase();
			secondaryDatabase = getDatabase2();
		} else {
			database = getNcbiDatabase();
			secondaryDatabase = getNcbiDatabase2();
		}

		// VALIDATION
		ValidationDelegate delegate = getValidation();
		String seqString = getSequenceString();
		if (seqString == null) {
			delegate
					.setFormComponent((IFormComponent) getComponent("sequenceString"));
			delegate.recordFieldInputValue(seqString);
			delegate.record(getMessages().getMessage("enterSequenceString"),
					null);
		}
		if (seqString != null) {
			seqString = seqString.replace(" ", "").trim();
			if (!SeqUtil.proteinSequenceIsValid(seqString)) {
				delegate
						.setFormComponent((IFormComponent) getComponent("sequenceString"));
				delegate.recordFieldInputValue(seqString);
				delegate.record(getMessages().getMessage(
						"invalidSequenceString"), null);
			}
		}
		if (secondaryDatabase.getPath() != null && getNumberOfIterations() < 2) {
			delegate
					.setFormComponent((IFormComponent) getComponent("numberOfIterations"));
			delegate.recordFieldInputValue(String
					.valueOf(getNumberOfIterations()));
			delegate.record(getMessages().getMessage("tooFewIterations"), null);
		}
		if (delegate.getHasErrors()) {
			return null;
		}

		// PRIMARY QUERY
		BlastQuery query = new BlastQuery();
		query.setCompositionalAdjustments(getCompositionalAdjustments());
		query.setLowComplexityFilter(getLowComplexity());
		query.setMaskForLookupTableOnlyFilter(getMaskLookup());
		query.setSequenceName(getSequenceName());
		Date enterDate = new Date();
		query.setEnterDate(new Timestamp(enterDate.getTime()));
		int period = getPeriod();
		query.setPeriod(period);
		Date nextSearchDate = Utils.addDays(enterDate, period);
		query.setNextSearchDate(new Timestamp(nextSearchDate.getTime()));
		query.setEvalue(getEValue());
		query.setIterations(getNumberOfIterations());
		query.setSequence(seqString);
		query.setCostToExtendAGap(getCostToExtendAGap());
		query.setCostToOpenAGap(getCostToOpenAGap());
		query.setEvalueMultipass(getEValueMultipass());
		query.setMatrix(getMatrix());
		query.setWordSize(getWordSize());
		query.setUser(getUser());
		query.setDatabasePath(database.getPath());
		query.setDatabaseName(database.getName());
		query.setCommandLineOptions(getCommandLineOptions());
		query.setAskingForSearch(true);
		query.setNotifyByMail(getNotifyByMail());
		query.setPhiPattern(getPhiPattern());
		query.setPssm(getPssm());
		query.setSearchLocation(getSearchLocation());
		query.setNumberOfHits(0);
		query.setNumberOfNewHits(0);
		query.setMaxNumberOfHits(getMaxNumberOfHits());
		IUploadFile file = getRestartFile();
		if (file != null) {
			String fileName = file.getFileName();
			query.setInputAlignmentFileName(fileName);
			InputStream is = file.getStream();
			try {
				query.setInputAlignmentFile(Hibernate.createBlob(is));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (getRestartFileName() != null && getRestartFileBlob() != null) {
			query.setInputAlignmentFileName(getRestartFileName());
			query.setInputAlignmentFile(getRestartFileBlob());
		}

		// SECONDARY QUERY
		if (secondaryDatabase.getPath() != null) {
			SecondaryBlastQuery query2 = new SecondaryBlastQuery();
			query2.setCompositionalAdjustments(getCompositionalAdjustments2());
			query2.setLowComplexityFilter(getLowComplexity2());
			query2.setMaskForLookupTableOnlyFilter(getMaskLookup2());
			query2.setEvalue(getEValue2());
			query2.setCostToExtendAGap(getCostToExtendAGap2());
			query2.setCostToOpenAGap(getCostToOpenAGap2());
			query2.setMatrix(getMatrix2());
			query2.setWordSize(getWordSize2());
			query2.setDatabasePath(secondaryDatabase.getPath());
			query2.setDatabaseName(secondaryDatabase.getName());
			query2.setCommandLineOptions(getCommandLineOptions2());
			query.setSecondaryQuery(query2);
			query.setDoubleSearch(true);
		}

		getPersistenceService().globalDAO().insertQuery(query);

		setInfoMessage(getMessages().getMessage("queryAdded"));
		return null;
	}
}