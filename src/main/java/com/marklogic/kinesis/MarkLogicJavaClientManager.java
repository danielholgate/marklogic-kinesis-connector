package com.marklogic.kinesis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.apache.log4j.Logger;

<<<<<<< HEAD
public class MarkLogicJavaClientManager {
	private static final Logger LOG = Logger.getLogger(MarkLogicJavaClientManager.class.getName());

	public static enum DocumentType {
		XML, JSON, TEXT, BINARY;
	}

	private DatabaseClient _dbClient = null;
	private String _host;
	private int _port;
	private String _user;
	private String _password;
	private XMLDocumentManager _xmldocMgr;
	private JSONDocumentManager _jsondocMgr;
	private BinaryDocumentManager _binaryDocMgr;
	private TextDocumentManager _txtDocMgr;

	MarkLogicJavaClientManager(String host, int port, String username, String password) {
		this._host = host;
		this._port = port;
		this._user = username;
		this._password = password;
	}

	public void open() {
		LOG.info("Opening ML client to " + this._host + ":" + this._port);
		if (this._dbClient == null) {
			this._dbClient = DatabaseClientFactory.newClient(this._host, this._port, this._user, this._password,
					DatabaseClientFactory.Authentication.DIGEST);

			this._xmldocMgr = this._dbClient.newXMLDocumentManager();
			this._jsondocMgr = this._dbClient.newJSONDocumentManager();
			this._binaryDocMgr = this._dbClient.newBinaryDocumentManager();
			this._binaryDocMgr.setMetadataExtraction(BinaryDocumentManager.MetadataExtraction.PROPERTIES);
			this._txtDocMgr = this._dbClient.newTextDocumentManager();
		}
	}

	public void releaseClient() {
		if (this._dbClient != null) {
			this._dbClient.release();
		}
	}

	public boolean insertDocument(String docType, String collection, String uri, byte[] docContent) {
		DocumentType type = DocumentType.valueOf(docType);
		return insertDocument(type, collection, uri, docContent);
	}

	public boolean insertDocument(DocumentType docType, String collection, String uri, byte[] docContent) {
		boolean insertResult = true;

		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		if ((collection != null) && (collection.length() > 0)) {
			metadataHandle.withCollections(new String[] { collection });
		}
		try {
			switch (docType) {
			case XML:
				DOMHandle domHandle = new DOMHandle();
				domHandle.fromBuffer(docContent);
				this._xmldocMgr.write(uri, metadataHandle, domHandle);
				break;
			case JSON:
				JacksonHandle jsHandle = new JacksonHandle();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jn = mapper.readTree(docContent);
				jsHandle.set(jn);
				JSONDocumentManager jsondocMgr = this._dbClient.newJSONDocumentManager();
				jsondocMgr.write(uri, metadataHandle, jsHandle);
				break;
			case BINARY:
				BytesHandle content = new BytesHandle(docContent);
				this._binaryDocMgr.write(uri, metadataHandle, content);
				break;
			case TEXT:
				StringHandle txtContent = new StringHandle(new String(docContent));
				this._txtDocMgr.write(uri, metadataHandle, txtContent);

				break;
			default:
				LOG.info("Default. Inserting " + uri + " as text");
				StringHandle txtContent2 = new StringHandle(new String(docContent));
				this._txtDocMgr.write(uri, metadataHandle, txtContent2);
			}
		} catch (ForbiddenUserException fue) {
			LOG.error("User provied could not write to MarkLogic " + fue.getMessage());
			insertResult = false;
		} catch (FailedRequestException | ResourceNotFoundException e) {
			LOG.error("Failed to write to MarkLogic " + e.getMessage());
			LOG.debug(e);
			insertResult = false;
		} catch (Exception e) {
			LOG.info("Failed to write to MarkLogic " + e.getMessage());
			insertResult = false;
		}
		return insertResult;
	}

	public boolean testConnection(String docURI) {
		boolean result = insertDocument("TEXT", "", docURI, new String("").getBytes());
		return result;
	}
=======
public class MarkLogicJavaClientManager
{
  private static final Logger LOG = Logger.getLogger(MarkLogicJavaClientManager.class.getName());
  
  public static enum DocumentType
  {
    XML,  JSON,  TEXT,  BINARY;
    
    private DocumentType() {}
  }
  
  private DatabaseClient _dbClient = null;
  private String _host;
  private int _port;
  private String _user;
  private String _password;
  private XMLDocumentManager _xmldocMgr;
  private JSONDocumentManager _jsondocMgr;
  private BinaryDocumentManager _binaryDocMgr;
  private TextDocumentManager _txtDocMgr;
  
  MarkLogicJavaClientManager(String host, int port, String username, String password)
  {
    this._host = host;
    this._port = port;
    this._user = username;
    this._password = password;
  }
  
  public void open()
  {
    LOG.info("Opening ML client to " + this._host + ":" + this._port);
    if (this._dbClient == null)
    {
      this._dbClient = DatabaseClientFactory.newClient(this._host, this._port, this._user, this._password, DatabaseClientFactory.Authentication.DIGEST);
      
      this._xmldocMgr = this._dbClient.newXMLDocumentManager();
      this._jsondocMgr = this._dbClient.newJSONDocumentManager();
      this._binaryDocMgr = this._dbClient.newBinaryDocumentManager();
      this._binaryDocMgr.setMetadataExtraction(BinaryDocumentManager.MetadataExtraction.PROPERTIES);
      this._txtDocMgr = this._dbClient.newTextDocumentManager();
    }
  }
  
  public void releaseClient()
  {
    if (this._dbClient != null) {
      this._dbClient.release();
    }
  }
  
  public boolean insertDocument(String docType, String collection, String uri, byte[] docContent)
  {
    DocumentType type = DocumentType.valueOf(docType);
    return insertDocument(type, collection, uri, docContent);
  }
  
  public boolean insertDocument(DocumentType docType, String collection, String uri, byte[] docContent)
  {
    boolean insertResult = true;
    
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    if ((collection != null) && (collection.length() > 0)) {
      metadataHandle.withCollections(new String[] { collection });
    }
    try
    {
      switch (docType)
      {
      case XML: 
        DOMHandle domHandle = new DOMHandle();
        domHandle.fromBuffer(docContent);
        this._xmldocMgr.write(uri, metadataHandle, domHandle);
        break;
      case JSON: 
        JacksonHandle jsHandle = new JacksonHandle();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(docContent);
        jsHandle.set(jn);
        JSONDocumentManager jsondocMgr = this._dbClient.newJSONDocumentManager();
        jsondocMgr.write(uri, metadataHandle, jsHandle);
        break;
      case BINARY: 
        BytesHandle content = new BytesHandle(docContent);
        this._binaryDocMgr.write(uri, metadataHandle, content);
        break;
      case TEXT: 
        StringHandle txtContent = new StringHandle(new String(docContent));
        this._txtDocMgr.write(uri, metadataHandle, txtContent);
        
        break;
      default: 
        LOG.info("Default. Inserting " + uri + " as text");
        StringHandle txtContent2 = new StringHandle(new String(docContent));
        this._txtDocMgr.write(uri, metadataHandle, txtContent2);
      }
    }
    catch (ForbiddenUserException fue)
    {
      LOG.error("User provied could not write to MarkLogic " + fue.getMessage());
      insertResult = false;
    }
    catch (FailedRequestException|ResourceNotFoundException e)
    {
      LOG.error("Failed to write to MarkLogic " + e.getMessage());
      LOG.debug(e);
      insertResult = false;
    }
    catch (Exception e)
    {
      LOG.info("Failed to write to MarkLogic " + e.getMessage());
      insertResult = false;
    }
    return insertResult;
  }
  
  public boolean testConnection(String docURI)
  {
    boolean result = insertDocument("TEXT", "", docURI, new String("").getBytes());
    return result;
  }
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
}
