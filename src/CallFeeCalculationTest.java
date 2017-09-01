import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CallFeeCalculationTest {


	@Test
	public void リストの長さと中身のテスト() {
		CallFeeCalculation ccc = new CallFeeCalculation();
		List<String> logList = ccc.readLog();

		assertEquals(20, logList.size());
		assertEquals("0 *************************", logList.get(0));
		assertEquals("1 090-1234-0001", logList.get(1));
		assertEquals("2 C1 090-1234-0002", logList.get(2));
		assertEquals("2 C1 090-1234-0003", logList.get(3));
		assertEquals("5 2004/06/04 03:34 003 090-1234-0002", logList.get(4));
		assertEquals("5 2004/06/04 13:50 010 090-1234-9999", logList.get(5));
		assertEquals("0 *************************", logList.get(6));
		assertEquals("1 090-1234-0002", logList.get(7));
		assertEquals("2 C1 090-1234-0001", logList.get(8));
		assertEquals("2 C1 090-1234-0003", logList.get(9));
		assertEquals("2 E1", logList.get(10));
		assertEquals("5 2004/06/05 17:50 010 090-1234-9999", logList.get(11));
		assertEquals("5 2004/06/06 12:34 007 090-1234-0003", logList.get(12));
		assertEquals("0 *************************", logList.get(13));
		assertEquals("1 090-1234-7777", logList.get(14));
		assertEquals("5 2004/06/06 13:50 010 090-1234-9999", logList.get(15));
		assertEquals("5 2004/06/13 13:50 010 090-1234-9999", logList.get(16));
		assertEquals("5 2004/06/20 13:50 010 090-1234-9999", logList.get(17));
		assertEquals("5 2004/06/27 13:50 010 090-1234-9999", logList.get(18));
		assertEquals("0 *************************", logList.get(19));
	}

	@Test
	public void 登録されたデータのテスト() {
		CallFeeCalculation ccc = new CallFeeCalculation();
		List<String> logList = ccc.readLog();
		ccc.registerLogData(logList);

		assertEquals(3, ccc.phoneNumbers.size());
		assertEquals("090-1234-0001", ccc.phoneNumbers.get(0));
		assertEquals("090-1234-0002", ccc.phoneNumbers.get(1));
		assertEquals("090-1234-7777", ccc.phoneNumbers.get(2));
		assertEquals(3, ccc.surviceCodeMap.size());
		assertEquals(1, ccc.surviceCodeMap.get("090-1234-0001").size());
		assertEquals(2, ccc.surviceCodeMap.get("090-1234-0002").size());
		assertEquals(0, ccc.surviceCodeMap.get("090-1234-7777").size());

	}

}
