package dk.fakeinfo.repository;

import dk.fakeinfo.model.Town;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class TownRepository {

    private final JdbcClient jdbcClient;

    public TownRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public int countTowns() {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM postal_code")
            .query(Integer.class)
            .single();
        return count == null ? 0 : count;
    }

    public Town findTownByOffset(int offset) {
        return jdbcClient.sql("""
                SELECT postal_code, town_name
                FROM postal_code
                ORDER BY postal_code
                OFFSET :offset LIMIT 1
                """)
            .param("offset", offset)
            .query((rs, rowNum) -> new Town(
                rs.getString("postal_code"),
                rs.getString("town_name")
            ))
            .single();
    }
}
