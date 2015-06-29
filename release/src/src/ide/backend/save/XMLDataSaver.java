package ide.backend.save;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ide.backend.runtime.ModelCollection;
import ide.backend.exceptions.FileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.Costume;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireFrameModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.conditions.IfBlockModel;
import ide.backend.model.conditions.IfElseModel;
import ide.backend.model.conditions.WhileModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.event.RefEventModel;
import ide.backend.model.function.AccessModel;
import ide.backend.model.function.EmitModel;
import ide.backend.model.function.FunctionCallModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.function.HandlerModel;
import ide.backend.model.function.ReturnModel;
import ide.backend.model.locks.LockModel;
import ide.backend.model.locks.UnLockModel;
import ide.backend.model.math.RandomModel;
import ide.backend.model.operator.OperatorModel;
import ide.backend.model.operator.UnOperatorModel;
import ide.backend.model.other.SleepModel;
import ide.backend.model.physicModels.ChangeAppearanceModel;
import ide.backend.model.physicModels.HideModel;
import ide.backend.model.physicModels.MoveModel;
import ide.backend.model.physicModels.ShowModel;
import ide.backend.model.string.CharAtModel;
import ide.backend.model.string.ConcatModel;
import ide.backend.model.string.LengthModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.model.variables.PrintModel;
import ide.backend.model.variables.RefMemberModel;
import ide.backend.model.variables.RefVariabelModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;


/**
 * Saves the program to an XML format.
 *  Void save(ModelCollection collection, String fileName): will save the program to a given file 
 *  	and save it's images to the parent directory of that file. (images are copied)
 *  String save(ModelCollection collection): will save the file to an XML format and return that format in a String.
 *  	Images remain in the place as specified when loading the program or loading the image. (images are not copied)
 * @author Matthijs Kaminski
 */
public class XMLDataSaver implements DataSaver<Element> {
	
	private Document _doc;
	private Element _root;
	
	@Override
	public String[] getAllowedExtensions() {
		String[] ext = {"xml"};
		return ext;
	}
	
	@Override
	public void save(ModelCollection collection, String fileName) throws FileException {
		try {
			createDoc();
			createRoot();
			saveImages(collection, new File(fileName).getParent());
			saveProgram(collection);
			writeToFile(fileName);
		} catch (ParserConfigurationException e) {
			throw new FileException(fileName);
		} catch (TransformerException e) {
			throw new FileException(fileName);
		}
	}
	/**
	 * Save images (costumes) to the directory where file was given.
	 * Images are save as JPG and there name is set to their given name as costume + _ + name of their class model.
	 * @param collection collection containing all the models of the program.
	 * @param parent name of the parent directory to which the program is saved.
	 */
	private void saveImages(ModelCollection collection, String parent) {
		for (ClassModel c : collection.getClassModels()) {
			for (HashMap.Entry<String, Costume> cos : c.getCostumes().entrySet()) {
				Image img =  cos.getValue().getImage().getImage();
				BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = bi.createGraphics();
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
				try {
					ImageIO.write(bi, "jpg", new File(parent + "/" + cos.getKey() + "_" + c.getName() ));
					//cos.getValue().setPath(parent + "/" + cos.getKey() + "_" + c.getName() );
					cos.getValue().setPath(cos.getKey() + "_" + c.getName() );
				} catch (IOException e) {
					System.out.println("error: saving images");
				}
			}
		}
		
	}

	@Override
	public String save(ModelCollection collection) {
		try {
			createDoc();
			createRoot();
			saveProgram(collection);
			return getStringFromDoc();
		} catch (ParserConfigurationException e) {
			return "";
		} catch (TransformerException e) {
			return "";
		}
	}
	
	/**
	 * Creates a new document.
	 * @throws ParserConfigurationException
	 */
	private void createDoc() throws ParserConfigurationException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		_doc = docBuilder.newDocument();
	}
	
	/**
	 * Creates a new root element in the document.
	 */
	private void createRoot(){
		_root = _doc.createElement("program");
		_doc.appendChild(_root);
	}
	
	/**
	 * Saves the content off the program.
	 */
	private void saveProgram(ModelCollection collection) {
		saveEvents(collection);
		saveClasses(collection);
		saveWireFrame(collection);
		
	}


	/**
	 * Saves the wireFrame of the program.
	 */
	private void saveWireFrame(ModelCollection collection) {
		_root.appendChild(collection.getWireFrameModel().save(this));
		
	}

	/**
	 * Saves all the classes of the program.
	 */
	private void saveClasses(ModelCollection collection) {
		Element classes = _doc.createElement("classes");
		for(ClassModel model: collection.getClassModels()) {
			classes.appendChild(model.save(this));
		}
		_root.appendChild(classes);
		
	}

	/**
	 * Saves all the events off the program.
	 */
	private void saveEvents(ModelCollection collection) {
		Element events = _doc.createElement("events");
		for(EventModel event : collection.getNonDefaultEventModels()){
			events.appendChild(event.save(this));
		}
		_root.appendChild(events);
		
	}
	
	/**
	 * Saves the created document to given file
	 * @param path path to document to save file.
	 * @throws TransformerException
	 */
	private void writeToFile(String path) throws TransformerException{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(_doc);
		StreamResult result = new StreamResult(new File(path));

		transformer.transform(source, result);
	}
	
	/**
	 * Converst the created document into a String.
	 * @return converted document.
	 * @throws TransformerException
	 */
	public String getStringFromDoc() throws TransformerException {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(_doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		transformer.transform(source, result);
	    return writer.getBuffer().toString();
	}

	@Override
	public Element saveBlock(ValueModel model) {
		Element out = _doc.createElement("value");
		out.appendChild(_doc.createTextNode(model.getContent()));
		savePosition(out, model);
		return out;
		
	}
	

	@Override
	public Element saveBlock(PrintModel model) {
		Element out = _doc.createElement("print");
		savePosition(out, model);
		if( model.getContent() != null)
			out.appendChild((Element) model.getContent().save(this)); 
		return out;
		
	}

	@Override
	public Element saveBlock(ClassModel model) {
		Element out = _doc.createElement("class");
		out.setAttribute("name", model.getName());
		//save input events.
		out.appendChild(saveClassInputEvents(model));
		//save output events.
		out.appendChild(saveClassEmits(model));
		//save memberVariables
		out.appendChild(saveClassMembers(model));
		//save handlers.
		out.appendChild(saveClassHandlers(model));
		//save functions.
		out.appendChild(saveClassFunctions(model));
		// Save floaters
		out.appendChild(saveFloaters(model));
		// save costumes
		out.appendChild(saveCostumes(model));
		return out;
	}
	
	/**
	 * Creates an element containing all the costumes of a given classmodel [model]
	 * @param model the Class from which the costumes are saved.
	 * @return Element containing all stored data
	 */
	private Element saveCostumes(ClassModel model) {
		Element costumes = _doc.createElement("costumes");
		for(HashMap.Entry<String, Costume> c : model.getCostumes().entrySet()){
			costumes.appendChild(saveCostume(c.getValue()));
		}
		return costumes;
	}

	/**
	 * Creates an element containing all the information of a given costume.
	 * @param c the costume being saved
	 * @return Element containing all stored data
	 */
	private Element saveCostume(Costume c) {
		Element costume = _doc.createElement("costume");
		costume.setAttribute("name", c.getName());
		costume.setAttribute("path", c.getPath());
		return costume;
	}

	/**
	 * Creates an element containing all the floating blocks within a given classmodel [model].
	 * @param model the Class from which the events are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveFloaters(ClassModel model) {
		Element events = _doc.createElement("floatingBlocks");
		for (BlockModel e : model.getFloaters()) {
			events.appendChild(e.save(this));
		}
		return events;
	}

	/**
	 * Creates an element containing all the input events of a given classmodel [model].
	 * @param model the Class from which the events are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveClassInputEvents(ClassModel model) {
		Element events = _doc.createElement("events");
		for (EventModel e : model.getInputEvents()) {
			Element in = _doc.createElement("inputEvent");
			in.setAttribute("type", e.getType());
			events.appendChild(in);
		}
		return events;
	}
	/**
	 * Creates an element containing all the output events of a given classmodel [model].
	 * @param model the Class from which the events are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveClassEmits(ClassModel model) {
		Element emits = _doc.createElement("emits");
		for (EventModel e : model.getOutputEvents()) {
			Element in = _doc.createElement("outputEvent");
			in.setAttribute("type", e.getType());
			emits.appendChild(in);
		}
		return emits;
	}

	/**
	 * Creates an element containing all of the information of the handlers of the given classmodel [model]
	 * @param model the Class from which the events are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveClassHandlers(ClassModel model) {
		Element handlers = _doc.createElement("handlers");
		for (HashMap.Entry<EventModel, ArrayList<HandlerModel>> entry : model.getHandlers().entrySet()) {
		  for (HandlerModel hand : entry.getValue()) {
			  handlers.appendChild(hand.save(this));
		  }
		}
		return handlers;
	}

	/**
	 * Creates an element containing all of the information of the functions of the given classmodel [model]
	 * @param model the Class from which the events are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveClassFunctions(ClassModel model) {
		Element functions = _doc.createElement("functions");
		for (FunctionModel func : model.getFunctions()) {
			functions.appendChild(func.save(this));
		}
		return functions;
	}

	/**
	 * Creates an element containing all of the information of the members of the given classmodel [model]
	 * @param model the Class from which the members are saved.
	 * @return Element containing all stored data.
	 */
	private Element saveClassMembers(ClassModel model) {
		Element members = _doc.createElement("memberVariables");
		for(HashMap.Entry<String, MemberModel> entry : model.getMembers().entrySet()){
			Element m = _doc.createElement("member");
			m.setAttribute("type", entry.getValue().getType().toString());
			m.setAttribute("name", entry.getKey());
			members.appendChild(m);
		}
		return members;
	}

	@Override
	public Element saveBlock(ConnectedBlocks model) {
		Element block = _doc.createElement("block");
		for (BlockModel b : model.getBlocks()) {
			block.appendChild((Element) b.save(this)); 
		}
		savePosition(block, model);
		return block;
	}

	@Override
	public Element saveBlock(EventModel model) {
		Element event = _doc.createElement("event");
		event.setAttribute("type", model.getType());
		for (String name : model.getMemberNames()) {
			Element member = _doc.createElement("member");
			member.setAttribute("type", model.getVarType(name).toString());
			member.setAttribute("name", name);
			event.appendChild(member);
		}
		return event;
		
	}

	@Override
	public Element saveBlock(FunctionModel model) {
		Element func = _doc.createElement("function");
		func.setAttribute("name", model.getName());
		// Save return
		if (model.getAmountofReturns() > 0) {
			Element ret = _doc.createElement("return");
			ret.setAttribute("type", model.getReturnType(0).toString());
			func.appendChild(ret);
		}
		//Save parameters
		Element params = _doc.createElement("params");
		for (BlockModel block : model.getParams()) {
			Element param = _doc.createElement("param");
			param.setAttribute("type", ((VariableModel)block).getType().toString());
			param.setAttribute("name", ((VariableModel)block).getName());
			params.appendChild(param);
		}
		func.appendChild(params);
		//save body of the function;
		func.appendChild(model.getConnectedBlocks().save(this));
		savePosition(func, model);
		return func;
		
	}

	@Override
	public Element saveBlock(HandlerModel model) {
		Element hand = _doc.createElement("handler");
		hand.setAttribute("name", model.getName());
		hand.setAttribute("event", model.getEvent());
		hand.appendChild(model.getConnectedBlocks().save(this));
		savePosition(hand, model);
		return hand;
		
	}

	@Override
	public Element saveBlock(RefVariabelModel model) {
		Element var = _doc.createElement("var");
		var.setAttribute("name", model.getName());
		savePosition(var, model);
		return var;
	}

	@Override
	public Element saveBlock(ReturnModel model) {
		Element out = _doc.createElement("return");
		for (BlockModel block : model.getReturnVars()) {
			out.appendChild(block.save(this));
		}
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(TypeModel model) {
		return null;
	}

	@Override
	public Element saveBlock(VariableModel model) {
		Element out = _doc.createElement("makeVar");
		out.setAttribute("name", model.getName());
		out.setAttribute("type", model.getType().toString());
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(SetModel model) {
		Element out = _doc.createElement("setVar");
		if (model.getVariableModel() != null)
			out.setAttribute("name", model.getVariableModel().getName());
		if (model.getContent() != null)
			out.appendChild(model.getContent().save(this));
		savePosition(out, model);
		return out;
		
	}

	@Override
	public Element saveBlock(RefEventModel refEventModel) {
		return null;
		
	}

	@Override
	public Element saveBlock(AccessModel accessModel) {
		Element out = _doc.createElement("access");
		out.setAttribute("name", accessModel.getMember());
		savePosition(out, accessModel);
		return out;
		
	}
	
	@Override
	public Element saveBlock(EmitModel model) {
		Element out = _doc.createElement("emit");
		out.setAttribute("name", model.getEventName());
		
		for(Entry<String, AbstractRefVariabelModel> a: model.getMembers().entrySet()) {
			if (a.getValue() != null) {
				Element child = _doc.createElement("var");
				child.setAttribute("name", a.getValue().getName());
				child.setAttribute("member", a.getKey());
				out.appendChild(child);			
			}
		}
		savePosition(out, model);
		
		return out;
	}

	@Override
	public Element saveBlock(FunctionCallModel model) {
		Element out = _doc.createElement("FunctionCall");
		out.setAttribute("name", model.getFuncName());
		//save value parameters
		Element params = _doc.createElement("params");
		for (AbstractRefVariabelModel var : model.getParams()) {
			if(var != null)
				params.appendChild(var.save(this));
			else
				params.appendChild(_doc.createElement("NULL"));
		}
		out.appendChild(params);
		//save returns.
		Element returns = _doc.createElement("returns");
		for (AbstractRefVariabelModel var : model.getReturns()) {
			if(var != null)
				returns.appendChild(var.save(this));
			else
				returns.appendChild(_doc.createElement("NULL"));
			
		}
		out.appendChild(returns);
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(InstanceModel model) {
		Element out = _doc.createElement("instance");
		out.setAttribute("name", model.getName());
		out.setAttribute("class", model.getClassName());
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(OperatorModel model) {
		Element out = _doc.createElement("operator");
		out.setAttribute("op", model.getOperator());
		if (model.getLeft() != null)
			out.appendChild(model.getLeft().save(this));
		else
			out.appendChild(_doc.createElement("NULL"));
		if (model.getRight() != null)
			out.appendChild(model.getRight().save(this));
		else
			out.appendChild(_doc.createElement("NULL"));
		savePosition(out, model);
		return out;
	}
	
	public Element saveBlock(WireModel model) {
		Element out = _doc.createElement("wire");
		out.setAttribute("from", model.getFrom());
		out.setAttribute("to", model.getTo());
		out.setAttribute("event", model.getEvent());
		return out;
	}

	@Override
	public Element saveBlock(WireFrameModel model) {
		Element out = _doc.createElement("wireframe");
		Element instances = _doc.createElement("instances");
		for (InstanceModel instance : model.getInstances()) {
			instances.appendChild(instance.save(this));
		}
		out.appendChild(instances);
		Element wires = _doc.createElement("wires");
		for (WireModel wire : model.getWires()) {
			wires.appendChild(wire.save(this));
		}
		out.appendChild(wires);
		return out;
	}

	@Override
	public Element saveBlock(IfBlockModel model) {
		Element out = _doc.createElement("if");
		Element condition = _doc.createElement("cond");
		if(model.getCondition() != null)
			condition.appendChild(model.getCondition().save(this));
		out.appendChild(condition);
		out.appendChild(model.getBody().save(this));
		savePosition(out, model);
		return out;
	}
	
	@Override
	public Element saveBlock(IfElseModel model) {
		Element out = _doc.createElement("if-else");
		Element condition = _doc.createElement("cond");
		if(model.getCondition() != null)
			condition.appendChild(model.getCondition().save(this));
		out.appendChild(condition);
		out.appendChild(model.getBodyIf().save(this));
		out.appendChild(model.getBodyElse().save(this));
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(WhileModel model) {
		Element out = _doc.createElement("while");
		Element condition = _doc.createElement("cond");
		if(model.getCondition() != null)
			condition.appendChild(model.getCondition().save(this));
		out.appendChild(condition);
		out.appendChild(model.getBody().save(this));
		savePosition(out, model);
		return out;
	}
	
	/**
	 * Save the position of the model
	 * @param elem Element being saved to.
	 * @param model model being saved.
	 */
	public void savePosition(Element elem, BlockModel model) {
		elem.setAttribute("x", "" + model.getPos().getX());
		elem.setAttribute("y", "" + model.getPos().getY());
	}
	
	
	/*
	 * SHOULD NOT BE SAVED IN THIS WAY.
	 */
	@Override
	public Element saveBlock(MemberModel model) {
		return null;
		
	}

	@Override
	public Element saveBlock(RefMemberModel model) {
		Element var = _doc.createElement("var");
		var.setAttribute("name", model.getName());
		return var;
	}

	@Override
	public Element saveBlock(ChangeAppearanceModel model) {
		Element out = _doc.createElement("appear");
		savePosition(out, model);
		if( model.getContent() != null)
			out.appendChild((Element) model.getContent().save(this));
		return out;
	}

	@Override
	public Element saveBlock(ForeverModel model) {
		Element out = _doc.createElement("forever");
		out.appendChild(model.getBody().save(this));
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(MoveModel model) {
		Element out = _doc.createElement("move");
		Element x = _doc.createElement("x");
		Element y = _doc.createElement("y");
		out.appendChild(x);
		out.appendChild(y);
		if(model.getX() != null)
				x.appendChild(model.getX().save(this));
		if(model.getY() != null)
			y.appendChild(model.getY().save(this));
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(LockModel model) {
		Element out = _doc.createElement("lock");
		savePosition(out, model);

		if (model.getVariableModel() != null)
			out.setAttribute("name", model.getVariableModel().getName());
		
		return out;
	}

	@Override
	public Element saveBlock(UnLockModel model) {
		Element out = _doc.createElement("unlock");
		savePosition(out, model);
		if (model.getVariableModel() != null)
			out.setAttribute("name", model.getVariableModel().getName());
		
		return out;
	}

	@Override
	public Element saveBlock(SleepModel model) {
		Element out = _doc.createElement("sleep");
		savePosition(out, model);
		if (model.getContent() != null)
			out.appendChild(model.getContent().save(this));
			//out.setAttribute("name", model.getVariableModel().getName());
		
		return out;
	}

	@Override
	public Element saveBlock(RandomModel model) {
		Element out = _doc.createElement("random");
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(ShowModel model) {
		Element out = _doc.createElement("show");
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(HideModel model) {
		Element out = _doc.createElement("hide");
		savePosition(out, model);
		return out;
	}

	@Override
	public Element saveBlock(ConcatModel model) {
		Element out = _doc.createElement("concat");
		savePosition(out, model);
		
		if (model.getLeft() != null) out.appendChild(model.getLeft().save(this));
		else out.appendChild(_doc.createElement("null"));
		if (model.getRight() != null) out.appendChild(model.getRight().save(this));
		
		return out;
	}

	@Override
	public Element saveBlock(LengthModel model) {
		Element out = _doc.createElement("length");
		savePosition(out, model);
		
		if (model.getContent() != null) out.appendChild(model.getContent().save(this));
		
		return out;
	}

	@Override
	public Element saveBlock(CharAtModel model) {
		Element out = _doc.createElement("charat");
		savePosition(out, model);
		
		if (model.getContent() != null) out.appendChild(model.getContent().save(this));
		else out.appendChild(_doc.createElement("null"));
		if (model.getIndex() != null) out.appendChild(model.getIndex().save(this));
		
		return out;
	}

	@Override
	public Element saveBlock(UnOperatorModel model) {
		Element out = _doc.createElement("operator");
		out.setAttribute("op", model.getOperator());
		if (model.getLeft() != null)
			out.appendChild(model.getLeft().save(this));
		else out.appendChild(_doc.createElement("NULL"));
		out.appendChild( _doc.createElement("dummy"));
		savePosition(out, model);
		return out;
	}
}
