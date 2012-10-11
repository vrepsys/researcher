package researcher.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;

import researcher.beans.BlastDatabase;
import researcher.blast.CompositionalAdjustments;
import researcher.blast.SearchLocation;


public class SelectionModels {

    public static class DatabaseSelectionModel implements IPropertySelectionModel, Serializable {

        private static final long serialVersionUID = 32713306707817415L;

        private List databases;

        /**
         * @param databases
         * @param emptyMessage
         *            select the message for an empty option. If it is
         *            specified, empty option (null) is added.
         */
        public DatabaseSelectionModel(List<BlastDatabase> databases, String emptyMessage) {
            if (emptyMessage != null) {
                databases = new ArrayList<BlastDatabase>(databases);
                BlastDatabase db = new BlastDatabase();
                db.setName(emptyMessage);
                db.setPath(null);
                databases.add(0, db);
            }
            this.databases = databases;
        }

        public DatabaseSelectionModel(List<BlastDatabase> databases) {
            this(databases, null);
        }

        public int getOptionCount() {
            return databases.size();
        }

        public Object getOption(int index) {
            return databases.get(index);
        }

        public String getLabel(int index) {
            BlastDatabase db = (BlastDatabase) databases.get(index);
            return db.getName();
        }

        public String getValue(int index) {
            return Integer.toString(index);
        }

        public Object translateValue(String value) {
            return getOption(Integer.parseInt(value));
        }
    }

    public static class SearchLocationSelectionModel implements IPropertySelectionModel {

        private List<SearchLocation> searchLocations;

        public SearchLocationSelectionModel() {
            searchLocations = new ArrayList<SearchLocation>();
            searchLocations.add(SearchLocation.NCBI);
            searchLocations.add(SearchLocation.LOCAL_SERVER);
        }

        public String getLabel(int i) {
            return searchLocations.get(i).getLabel();
        }

        public Object getOption(int i) {
            return searchLocations.get(i);
        }

        public int getOptionCount() {
            return searchLocations.size();
        }

        public String getValue(int index) {
            return Integer.toString(index);
        }

        public Object translateValue(String value) {
            return getOption(Integer.parseInt(value));
        }
    }

    public static class CompositionalAdjustmentsModel implements IPropertySelectionModel {

        List<CompositionalAdjustments> list = new ArrayList<CompositionalAdjustments>();

        public CompositionalAdjustmentsModel() {
            list.add(CompositionalAdjustments.NO_AJUSTMENT);
            list.add(CompositionalAdjustments.COMPOSITION_BASED_STATISTICS);
            list.add(CompositionalAdjustments.CONDITIONAL_COMPOSITIONAL);
            list.add(CompositionalAdjustments.UNIVERSAL_COMPOSITIONAL);
        }

        public String getLabel(int arg0) {
            return list.get(arg0).getPsiblastName();
        }

        public Object getOption(int arg0) {
            return list.get(arg0);
        }

        public int getOptionCount() {
            return list.size();
        }

        public String getValue(int arg0) {
            return Integer.toString(arg0);
        }

        public Object translateValue(String value) {
            return getOption(Integer.parseInt(value));
        }

    }
}
