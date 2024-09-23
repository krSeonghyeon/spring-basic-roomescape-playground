package roomescape.member;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDao {
    private JdbcTemplate jdbcTemplate;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member save(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("INSERT INTO member(name, email, password, role) VALUES (?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPassword());
            ps.setString(4, member.getRole());
            return ps;
        }, keyHolder);

        return new Member(keyHolder.getKey().longValue(), member.getName(), member.getEmail(), "USER");
    }

    public Optional<Member> findByEmailAndPassword(String email, String password) {
        try {
            Member member = jdbcTemplate.queryForObject(
                "SELECT id, name, email, role FROM member WHERE email = ? AND password = ?",
                (rs, rowNum) -> new Member(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ),
                email, password
            );
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Member findByName(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT id, name, email, role FROM member WHERE name = ?",
                (rs, rowNum) -> new Member(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ),
                name
        );
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, name, email, role FROM member WHERE id = ?";
        try {
            Member member = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Member(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ),
                id
            );
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
