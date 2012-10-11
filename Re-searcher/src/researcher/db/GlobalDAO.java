package researcher.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import researcher.beans.BlastDatabase;
import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.beans.FastaSequence;
import researcher.beans.Hit;
import researcher.beans.SecondaryBlastQuery;
import researcher.beans.User;
import researcher.blast.SearchLocation;


public class GlobalDAO {

    private Session session;
    
    public Session getSession() {
        return session;
    }

    public GlobalDAO() {
        Session sess = HibernateUtil.openSession();
        sess.beginTransaction();        
        session = sess;
    }

    public GlobalDAO(Session session) {
        this.session = session;
    }

    public BlastQuery getQuery(Long queryId) {
        BlastQuery query = (BlastQuery) session.get(BlastQuery.class, queryId);        
        return query;
    }
    
    public void addHit(Hit hit){
        session.save(hit);
    }
    
    
    public List<Hit> getHitsBySubjectId(BlastQuery query, String subjectId) {
        List<Hit> hits = session.createCriteria(Hit.class).add(Restrictions.eq("query", query))
                .add(Restrictions.eq("subjectId", subjectId)).list();
        return hits;
    }
    
    public List<Hit> getHitsWithFastas(BlastQuery query) {
        List<Hit> hits = session.createCriteria(Hit.class).add(Restrictions.eq("query", query))
        				.setFetchMode("fastaSequence", FetchMode.JOIN).list();
        return hits;
    }
    
    public List<Hit> getNewHitsWithFastas(BlastQuery query) {
       List<Hit> hits = session.createCriteria(Hit.class).add(Restrictions.eq("query", query)).add(Restrictions.eq("unseen", new Boolean(true)))
              .setFetchMode("fastaSequence", FetchMode.JOIN).list();
       return hits;
   }

    public Set<Hit> getHits(long queryId) throws SQLException {
        Set<Hit> hits = ((BlastQuery) session.load(BlastQuery.class, queryId)).getHits();
        return hits;
    }
    
    public void saveFastaSequences(Set<FastaSequence> fastas){
        int cnt = 0;
        for (FastaSequence fasta : fastas) {
            session.save(fasta);
            cnt++;
            if (cnt % 20 == 0) {
                flush();
                clear();
            }
        }
    }

    public List<Hit> getHits(long queryId, Date dateFrom, Date dateTill) throws SQLException {
        BlastQuery bq = (BlastQuery) session.load(BlastQuery.class, new Long(queryId));
        Criteria criteria = session.createCriteria(Hit.class).add(Restrictions.eq("query", bq));
        if (dateFrom != null) {
            criteria = criteria.add(Restrictions.ge("hitDate", dateFrom));
        }
        if (dateTill != null) {
            criteria = criteria.add(Restrictions.le("hitDate", dateTill));
        }
        List<Hit> hits = criteria.list();
        return hits;
    }

    public void insertBlastDatabase(BlastDatabase bdb) {
        session.save(bdb);
    }

    public List<BlastDatabase> getBlastDatabases() {
        List<BlastDatabase> result = session.createQuery("from BlastDatabase").list();
        return result;
    }

    public void insertQuery(BlastQuery query) {
        session.save(query);
    }

    public void updateQuery(BlastQuery query) {
        session.update(query);
    }

    public void insertSecondaryBlastQuery(SecondaryBlastQuery sq) {
        session.save(sq);
    }

    public void updateHitsIsNew(long queryId, boolean isNew) {
        String hql = "update Hit set unseen = :isNew where queryId = :queryId";
        session.createQuery(hql).setParameter("isNew", isNew).setParameter("queryId", queryId)
                .executeUpdate();
    }

    public List<BlastQuery> getQueries(User user) {        
        List<BlastQuery> queries = session.createQuery("from BlastQuery where user = :user")
                                            .setParameter("user", user).list();
        return queries;
    }

    public List<BlastQuery> getAllQueries() {
        List<BlastQuery> queries = session.createQuery("from BlastQuery").list();
        return queries;
    }

    public void deleteQuery(long queryId) throws SQLException {
        BlastQuery q = (BlastQuery) session.load(BlastQuery.class, queryId);
        Set<Hit> hits = q.getHits();        
        for (Hit hit : hits) {
            FastaSequence seq = hit.getFastaSequence();
            if (seq != null) {
                if (seq.getHits().size() < 2) {
                    session.delete(seq);
                }                
            }
            session.delete(hit);
        }
        session.delete(q);
    }

    public User getUser(String password) {
        User user = (User) session.createCriteria(User.class).add(
                Restrictions.eq("password", password)).uniqueResult();
        return user;
    }
    
    public User getUserByUuid(String uuid) {
        User user = (User) session.createCriteria(User.class).add(
                Restrictions.eq("uuid", uuid)).uniqueResult();
        return user;
    }

    public User getUser(long id) {
        User user = (User) session.load(User.class, id);        
        return user;
    }

    public List<User> getUsers() {
        List<User> users = session.createQuery("from User").list();
        return users;
    }

    public void saveOrUpdatetUser(User user) {
        session.saveOrUpdate(user);
    }

    public void deleteUser(long userId) {
        User user = (User) session.get(User.class, userId);
        if (user != null)
            session.delete(user);
    }

    public void deleteBlastDatabase(long databaseId) {
        BlastDatabase bd = (BlastDatabase) session.get(BlastDatabase.class, new Long(databaseId));
        if (bd != null)
            session.delete(bd);
    }

    /**
     * returns application configuration object, null if configuration does not
     * exist yet
     * 
     * @param session
     * @return
     */
    public Configuration getConfiguration() {
        List<Configuration> configs = session.createQuery("from Configuration").list();
        if (configs.size() == 0) {
            return null;
        }
        Configuration config = configs.get(0);
        return config;
    }

    /**
     * deletes the old configuration and saves the new one
     * 
     * @param session
     * @param config
     *            new configuration bean
     */
    public void setConfiguration(Configuration config) {
        session.createQuery("delete from Configuration").executeUpdate();
        session.save(config);
    }

    /**
     * Selects BlastQueries if it's time to perform a search with them
     * 
     * @return
     */
    public List<BlastQuery> getBlastQueriesForSearch(SearchLocation sl) {
        List<BlastQuery> l = session.createCriteria(BlastQuery.class).add(
                Restrictions.or(Restrictions.and(Restrictions.le("nextSearchDate", new Date()),
                        Restrictions.isNotNull("nextSearchDate")), Restrictions.eq(
                        "askingForSearch", true))).add(Restrictions.eq("searchLocation", sl))
                .setFetchMode("secondaryQuery", FetchMode.JOIN)
                .setFetchMode("hits", FetchMode.JOIN).setResultTransformer(
                        new DistinctRootEntityResultTransformer()).list();
        return l;
    }

    public Hit getHit(long hitId) {
        return (Hit) session.load(Hit.class, hitId);
    }
    
    public void flush(){
    	session.flush();
    }
    
    public void clear(){
    	session.clear();
    }

    public void close() {        
        session.getTransaction().commit();
        session.close();
    }

    public User getUserByUsername(String username) {
        User user = (User) session.createCriteria(User.class).add(
                Restrictions.eq("username", username)).uniqueResult();        
        return user;        
    }

    public FastaSequence getFastaSequenceBySeqId(String id) {
        return (FastaSequence) session.createQuery(
        "from FastaSequence where sequenceId = ?").setString(0, id).uniqueResult();
    }

    public void saveFastaSequence(FastaSequence fastaseq) {
        session.save(fastaseq);
        
    }

   public List<Hit> getAllHits() {
      List<Hit> hits = session.createCriteria(Hit.class).list();
      return hits;
   }
}
