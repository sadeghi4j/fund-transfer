package com.sadeghi.fundtransfer;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Description of file goes here
 *
 * @author Ali Sadeghi
 * Created at 2022/05/02 - 2:26 PM
 */
@SpringBootTest(classes = FundTransferApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestPropertySource(locations = "classpath:application-h2.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTestClass {

}
