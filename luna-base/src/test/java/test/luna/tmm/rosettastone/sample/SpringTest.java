package test.luna.tmm.rosettastone.sample;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:resources/luna-context-application.xml")
public class SpringTest {
//	@Resource
	
	@org.junit.Test
	public void testSome(){
		
	}
}
