package billiongoods.server.services.sales.impl;

import billiongoods.server.services.sales.SalesOperation;
import billiongoods.server.services.sales.SalesOperationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FileSalesOperationManager implements SalesOperationManager, InitializingBean {
	private File salesOperationFile;
	private SalesOperation salesOperation = null;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.sales.FileSalesOperationManager");

	public FileSalesOperationManager() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.salesOperation = loadSalesDates();
	}

	@Override
	public SalesOperation getSalesOperation() {
		if (salesOperation != null && salesOperation.isExpired()) {
			salesOperation = saveSalesDates(null);
		}
		return salesOperation;
	}

	@Override
	public void openSales() {
		this.salesOperation = saveSalesDates(null);
	}

	@Override
	public void closeSales(LocalDateTime stopSales, LocalDateTime startSales) {
		this.salesOperation = saveSalesDates(new SalesOperation(stopSales, startSales));
	}

	private SalesOperation loadSalesDates() {
		if (!salesOperationFile.exists()) {
			return null;
		}

		try (final ObjectInputStream in = new ObjectInputStream(new FileInputStream(salesOperationFile))) {
			return (SalesOperation) in.readObject();
		} catch (Exception ex) {
			log.error("Sales Operation can't be loaded", ex);
			return null;
		}
	}

	private SalesOperation saveSalesDates(SalesOperation salesOperation) {
		try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(salesOperationFile))) {
			out.writeObject(salesOperation);
			return salesOperation;
		} catch (Exception ex) {
			log.error("Sales Operation can't be stored", ex);
			return null;
		}
	}

	public void setSalesOperationFile(Resource salesOperationFile) throws IOException {
		this.salesOperationFile = salesOperationFile.getFile();

		Files.createDirectories(this.salesOperationFile.toPath().getParent());
	}
}
