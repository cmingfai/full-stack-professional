package cc.oolong.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {
    @Mock
    private ResultSet resultSet;

    private CustomerRowMapper underTest;

    @BeforeEach
    void setUp() {
        underTest=new CustomerRowMapper();
    }

    @Test
    void mapRow() throws Exception {

        // given
        Customer expected=new Customer(1,"Alex","alex@gmail.com",22);

        when(resultSet.getInt("id")).thenReturn(expected.getId());
        when(resultSet.getString("name")).thenReturn(expected.getName());
        when(resultSet.getString("email")).thenReturn(expected.getEmail());
        when(resultSet.getInt("age")).thenReturn(expected.getAge());

        // when
        Customer actual=underTest.mapRow(resultSet,1);

        // then
        assertThat(actual).isEqualTo(expected);

    }
}