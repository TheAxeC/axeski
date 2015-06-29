package ide.backend.load;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.LoadException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.Costume;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.conditions.IfBlockModel;
import ide.backend.model.conditions.IfElseModel;
import ide.backend.model.conditions.WhileModel;
import ide.backend.model.event.EventModel;
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
import ide.backend.model.variables.AbstractVariableModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.model.variables.PrintModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.runtime.ModelCollection;
import ide.backend.variables.VariableFactory;

/**
 * Loads a program from an XML file.
 * @author Axel
 *
 */
public class XMLDataLoader extends DataLoader {
	
	private ModelCollection _collection;
	
	// All function calls are placed in this hashmap
	// After a class has been completed all function calls are linked
	private HashMap<FunctionCallModel, String> _undefinedCalls;
	//parent direcotry,
	private String _parentDir = "";
	
	private ClassModel _currentClass;
 
	@Override
	public void load(ModelCollection collection, String fileName) throws LoadException, FileException {
		_collection = collection;
		_parentDir = new File(fileName).getParent() + "/";
		
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = fact.newDocumentBuilder();
			Document doc = builder.parse(fileName);
			
			buildProgram(doc);
		} catch (ParserConfigurationException e) {
			throw new LoadException(fileName);
		} catch (SAXException e) {
			throw new LoadException(fileName);
		} catch (IOException e) {
			throw new LoadException(fileName);
		} catch (NullPointerException e) {
			throw new LoadException(fileName);
		}
	}
	
	@Override
	public void loadString(ModelCollection collection, String content) throws LoadException, FileException {
		_collection = collection;
		
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));

			Document doc = db.parse(is);
			
			buildProgram(doc);
		} catch (ParserConfigurationException e) {
			throw new LoadException("");
		} catch (SAXException e) {
			throw new LoadException("");
		} catch (IOException e) {
			throw new LoadException("");
		} catch (NullPointerException e) {
			throw new LoadException("");
		}
	}
	
	/**
	 * Trim all non-elements from a nodelist
	 * @param nl
	 * @return all elements from a nodelist
	 */
	private ArrayList<Element> trimNodeList(NodeList nl) {
		ArrayList<Element> ret = new ArrayList<Element>();
		for(int i=0; i<nl.getLength(); i++) {
			if(nl.item(i).getNodeType()==Node.ELEMENT_NODE)
				ret.add((Element) nl.item(i));
		}
		return ret;
	}
	
	/**
	 * Builds the actual program
	 * @param doc
	 * @throws FileException 
	 */
	private void buildProgram(Document doc) throws FileException {
		Element root = doc.getDocumentElement();
		
		if (!root.getNodeName().equals("program")) throw new FileException("File structure is wrong.");
		
		ArrayList<Element> nl = trimNodeList(root.getChildNodes());
		if(nl.size() != 3) throw new FileException("File structure is wrong.");
		
		procesEvents((Element) nl.get(0));
		procesClasses((Element) nl.get(1));
		procesWireFrame((Element) nl.get(2));
	}
	
	/**
	 * Load the events
	 * @param element
	 */
	private void procesEvents(Element element) throws FileException {
		checkNodeName(element, "events");
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				EventModel m = procesEvent((Element) nl.get(i));
				if (_collection.getEventModel(m.getType()) == null)
					_collection.addEventModel(m);	
			}
	}
	
	/**
	 * Load the classes
	 * @param element
	 */
	private void procesClasses(Element element) throws FileException {
		checkNodeName(element, "classes");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				ClassModel m = procesClass((Element) nl.get(i));
				if (_collection.getClassModel(m.getName()) == null)
					_collection.addClassModel(m);
			}
	}
	
	/**
	 * Load the wireFrame
	 * @param element
	 */
	private void procesWireFrame(Element element) throws FileException {
		checkNodeName(element, "wireframe");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		procesInstances((Element)nl.get(0));
		procesWires((Element)nl.get(1));
		
	}
	
	/**
	 * Loads the wires.
	 * @param element element containing the wires.
	 * @throws FileException
	 */
	private void procesWires(Element element) throws FileException {
		checkNodeName(element, "wires");
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++){
				_collection.addWireModel(procesWire((Element) nl.get(i)));
			}
		
	}

	/**
	 * Loads a wire.
	 * @param element element containing a wire.
	 * @return Created wireModel form description in element.
	 * @throws FileException
	 */
	private WireModel procesWire(Element element) throws FileException{
		checkNodeName(element, "wire");
		InstanceModel from, to;
		from = _collection.getInstanceModel(element.getAttribute("from"));
		to = _collection.getInstanceModel(element.getAttribute("to"));
		if(to == null || from == null)
			throw new FileException("Instance "+ element.getAttribute("from") + " OR " +  element.getAttribute("to") + " not defined");
		EventModel event = _collection.getEventModel(element.getAttribute("event"));
		if(event == null)
			throw new FileException("Event "+ element.getAttribute("event") + " not defined");
		
		return new WireModel(_collection.getWireFrameModel(), from, to, event);
	}

	/**
	 * Loads all the instances of the program.
	 * @param element element containing the instances.
	 * @throws FileException
	 */
	private void procesInstances(Element element) throws FileException {
		checkNodeName(element, "instances");
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++){
				_collection.addInstanceModel(procesInstance((Element) nl.get(i)));
			}
	}
	
	/**
	 * Loads an instance from a given element.
	 * @param item element containing the instance
	 * @return InstanceModel created from the element.
	 * @throws FileException
	 */
	private InstanceModel procesInstance(Element item) throws FileException {
		checkNodeName(item, "instance");
		ClassModel cl = _collection.getClassModel(item.getAttribute("class"));
		if (cl == null)
			throw new FileException("Class of instance:"+ item.getAttribute("name") +" not defined.");
		InstanceModel out = new InstanceModel(_collection.getWireFrameModel(), cl , item.getAttribute("name"));
		out.setPos(new Point((int) Double.parseDouble(item.getAttribute("x")), (int) Double.parseDouble(item.getAttribute("y"))));
		return out;
	}

	/**
	 * Proces an event node in the xml file
	 * @param element, the element in the xml file
	 * @return the newly created EventModel
	 * @throws FileException
	 */
	private EventModel procesEvent(Element element) throws FileException {
		checkNodeName(element, "event");
		
		String name = element.getAttribute("type");
		EventModel evt = new EventModel(null, name);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				evt.addMember(procesEventMember(evt, (Element) nl.get(i)));
		
		return evt;
	}
	
	/**
	 * Creates a new event member
	 * @param parent, the parent model (the eventModel)
	 * @param element, the element in the xml file
	 * @return a new VariableModel
	 * @throws FileException
	 */
	private VariableModel procesEventMember(EventModel parent, Element element) throws FileException {
		checkNodeName(element, "member");
		
		String name = element.getAttribute("name");
		String type = element.getAttribute("type");
		
		if (parent.getMember(name) != null) return null;
		return new VariableModel(parent, name, VariableFactory.getType(type));
	}
	
	/**
	 * Creates a new classModel based on the given XML
	 * @param element, the XML element
	 * @return A new ClassModel
	 * @throws FileException
	 */
	private ClassModel procesClass(Element element) throws FileException {
		checkNodeName(element, "class");
		
		String name = element.getAttribute("name");
		ClassModel mdl = new ClassModel(name);
		_currentClass = mdl;
		_undefinedCalls = new HashMap<FunctionCallModel, String>();
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				procesClassContents(mdl, (Element) nl.get(i));
			}
		
		checkUndefinedFunctionCalls(mdl);
		
		return mdl;
	}

	/**
	 * Checks the undefined function calls
	 * @param mdl
	 */
	private void checkUndefinedFunctionCalls(ClassModel mdl) {
		// Extra: delegate unfound functions to another hashmap that links the functions
		ArrayList<FunctionCallModel> toRemove = new ArrayList<FunctionCallModel>();
		for(Entry<FunctionCallModel, String> elem: _undefinedCalls.entrySet()) {
			ArrayList<FunctionModel> funcs = mdl.getFunctions();
			for(FunctionModel f: funcs) {
				if (f.getName().equals(elem.getValue())) {
					// Set the element of the function
					elem.getKey().changeFunction(f);
					toRemove.add(elem.getKey());
				}
					
			}
		}
		for(FunctionCallModel f: toRemove) _undefinedCalls.remove(f);
		_undefinedCalls = null;
	}

	/**
	 * Proccesses the contents of a classModel
	 * @param mdl, the classModel
	 * @param elem, the XML element
	 * @throws FileException
	 */
	private void procesClassContents(ClassModel mdl, Element elem) throws FileException {
		String nm = elem.getNodeName();
		switch (nm) {
		case "events":
			procesInputEvents(mdl, elem);
			break;
		case "emits":
			procesOutputEvents(mdl, elem);
			break;
		case "handlers":
			procesHandlers(mdl, elem);
			break;
		case "functions":
			procesFunctions(mdl, elem);
			break;
		case "memberVariables":
			procesMemberVariables(mdl, elem);
			break;
		case "floatingBlocks":
			procesFloaters(mdl, elem);
			break;
		case "costumes":
			loadImages(mdl, elem);
			break;
		default:
			throw new FileException(" invalid element in Class");
		}
	}
	
	/**
	 * Load all images from this class
	 * @param clsMdl, the classModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void loadImages(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "costumes");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		for(Element elem: nl) {
			// Load all the images
			String name = elem.getAttribute("name");
			String path = elem.getAttribute("path");
			if (!name.equals("") && !path.equals("")) {
				if (!path.contains("/"))
					path = _parentDir + path;
				Costume costume = new Costume(name, path);
				mdl.addCostume(name, costume);
			}
		}
	}
	
	/**
	 * Proces all floating blocks
	 * @param clsMdl, the classModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesFloaters(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "floatingBlocks");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		for(Element elem: nl) {
			HashMap<String, AbstractVariableModel> vars = new HashMap<String, AbstractVariableModel>();
			
			for (HashMap.Entry<String, MemberModel> entry : mdl.getMembers().entrySet()) {
				vars.put(entry.getKey(), entry.getValue());
			}
			
			BlockModel m = procesBlockContent(null, elem, vars);
			if (m != null)
				mdl.addFloatBlock(m);
		}
	}
	
	/**
	 * Proces all ouput events
	 * @param clsMdl, the classModel
	 * @param element, the XML element
	 * @throws FileException 
	 */
	private void procesOutputEvents(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "emits");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesOutputEvent(mdl, (Element) nl.get(i));
	}
	
	/**
	 * Proces an individual output event
	 * @param mdl, the classModel
	 * @param element, the XML element 
	 */
	private void procesOutputEvent(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "outputEvent");
		
		String type = element.getAttribute("type");
		//int amount = Integer.parseInt(element.getAttribute("amount"));
		
		EventModel evt = _collection.getEventModel(type);
		if (evt == null) throw new FileException("Output event doesnt exist.");
		//for(int i=0; i<amount; i++)
		//	mdl.addOutputEvent(evt);
	}

	
	/**
	 * Proces all input events
	 * @param clsMdl, the classModel
	 * @param element, the XML element
	 * @throws FileException 
	 */
	private void procesInputEvents(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "events");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesInputEvent(mdl, (Element) nl.get(i));
	}
	
	
	/**
	 * Proces an individual input event
	 * @param mdl, the classModel
	 * @param element, the XML element 
	 */
	private void procesInputEvent(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "inputEvent");
		
		String type = element.getAttribute("type");
		
		EventModel evt = _collection.getEventModel(type);
		if (evt == null) throw new FileException("Input event doesnt exist.");
		mdl.addInputEvent(evt);
	}

	/**
	 * Process all member variables from a class
	 * @param clsMdl, the classModel
	 * @param element, the XML element
	 * @throws FileException 
	 */
	private void procesMemberVariables(ClassModel clsMdl, Element element) throws FileException {
		checkNodeName(element, "memberVariables");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesMemberVariable(clsMdl, (Element) nl.get(i));
	}
	
	/**
	 * Proces a single class Member
	 * @param mdl
	 * @param element
	 * @throws FileException
	 */
	private void procesMemberVariable(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "member");
		
		String name = element.getAttribute("name");
		String type = element.getAttribute("type");
		
		if (mdl.getMember(name) == null)
			mdl.addMember(name, VariableFactory.getType(type));
	}
	
	/**
	 * Proces all handlers from a class
	 * @param mdl, the ClassModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesHandlers(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "handlers");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesHandler(mdl, (Element) nl.get(i));
	}
	
	/**
	 * Proces all functions from a class
	 * @param mdl, the ClassModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesFunctions(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "functions");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesFunction(mdl, (Element) nl.get(i));
	}
	
	/**
	 * Create a function using an XML element
	 * @param mdl, the ClassModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesHandler(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "handler");
		checkBodyNotContains(element, "return");
		
		String name = element.getAttribute("name");
		String type = element.getAttribute("event");

		HandlerModel handler = new HandlerModel(name);
		
		EventModel evt = _collection.getEventModel(type);
		if (evt == null) handler.setEvent(null);
		else handler.setEvent(evt.makeReference(handler));
		
		HashMap<String, AbstractVariableModel> variables = new HashMap<String, AbstractVariableModel>();
		
		for (HashMap.Entry<String, MemberModel> entry : mdl.getMembers().entrySet()) {
			variables.put(entry.getKey(), entry.getValue());
		}
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		handler.addBlock(procesBlocks(nl.get(0), handler, variables));
		
		loadPosition(element, handler);
		mdl.addHandler(evt, handler);
	}
	
	/**
	 * Create a function using an XML element
	 * @param mdl, the ClassModel
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesFunction(ClassModel mdl, Element element) throws FileException {
		checkNodeName(element, "function");
		checkBodyNotContains(element, "access");
		
		String name = element.getAttribute("name");
		FunctionModel func = new FunctionModel(name);
		
		HashMap<String, AbstractVariableModel> variables = new HashMap<String, AbstractVariableModel>();

		for (HashMap.Entry<String, MemberModel> entry : mdl.getMembers().entrySet()) {
			variables.put(entry.getKey(), entry.getValue());
		}
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() > 2) {
			Element ret = nl.get(0);
			String type = ret.getAttribute("type");
			func.addReturn(new TypeModel(func, VariableFactory.getType(type)));
			procesParams(func, nl.get(1), variables);		
			func.addBlock(procesBlocks(nl.get(2), func, variables));
		} else {
			procesParams(func, nl.get(0), variables);		
			func.addBlock(procesBlocks(nl.get(1), func, variables));
		}
		loadPosition(element, func);
		
		if (mdl.functionNotExist(name))
			mdl.addFunction(func);
	}
	
	/**
	 * Proces the parameters from a function
	 * @param func, the function Model
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesParams(FunctionModel func, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "params");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				procesParam(func, (Element) nl.get(i), variables);
	}
	
	/**
	 * Proces a parameter from the XML
	 * @param func, the function to which the param will belong
	 * @param element, the XML element
	 * @throws FileException
	 */
	private void procesParam(FunctionModel func, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "param");
		
		String name = element.getAttribute("name");
		String type = element.getAttribute("type");
		
		VariableModel v = new VariableModel(func, name, VariableFactory.getType(type));
		variables.put(name, v);
		v.setName(name);
		func.addParam(v);
	}
	
	/**
	 * Proces the body from some block which allows all
	 * @param func, the function Model
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @throws FileException
	 */
	private ConnectedBlocks procesBlocks(Element element, BlockModel prnt, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "block");
		ConnectedBlocks b = new ConnectedBlocks(prnt);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				b.addBlock(procesBlockContent(b, (Element) nl.get(i), variables));
			}
		loadPosition(element, b);
		
		return b;
	}
	
	/**
	 * Proces the contents of a connected blocks
	 * @param blocks, the connected blocks
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @throws FileException
	 */
	private BlockModel procesBlockContent(BlockModel parent, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		String nm = element.getNodeName();
		switch (nm) {
		case "print":
			return procesPrintModel(parent, element, variables);
		case "appear":
			return procesAppearModel(parent, element, variables);
		case "setVar":
			return procesSetModel(parent, element, variables);
		case "if":
			return procesIfModel(parent, element, variables);
		case "if-else":
			return procesIfElseModel(parent, element, variables);
		case "while":
			return procesWhileModel(parent, element, variables);
		case "forever":
			return procesForeverModel(parent, element, variables);
		case "value":
			return procesValueModel(parent, element, variables);
		case "makeVar":
			return procesVariabelModel(parent, element, variables);
		case "access":
			return procesAccessModel(parent, element, variables);
		case "var":
			return procesRefVariabelModel(parent, element, variables);
		case "return":
			return procesReturnModel(parent, element, variables);
		case "emit":
			return procesEmitModel(parent, element, variables);
		case "FunctionCall":
			return procesFunctionCallModel(parent, element, variables);
		case "operator":
			return procesOperator(parent, element, variables);
		case "block":
			return procesBlocks(element, parent, variables);
		case "lock":
			return procesLock(parent, element, variables);
		case "unlock":
			return procesUnLock(parent, element, variables);
		case "sleep":
			return procesSleep(parent, element, variables);
		case "move":
			return procesMove(parent, element, variables);
		case "show":
			return procesShow(parent, element, variables);
		case "hide":
			return procesHide(parent, element, variables);
		case "random":
			return procesRandom(parent, element, variables);
		case "concat":
			return procesConcat(parent, element, variables);
		case "length":
			return procesLength(parent, element, variables);
		case "charat":
			return procesCharAt(parent, element, variables);
		case "null":
			return null;
		default:
			throw new FileException("element \"" + element.getNodeName() + "\" is not supported.");
		}
	}
	
	/**
	 * Proces an length model
	 * @param prnt, the parent of the length block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesLength(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		LengthModel out = new LengthModel(parent);
		loadPosition(element, out);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() > 0) {
			BlockModel left = procesBlockContent(out, nl.get(0), variables);
			out.setContent(left);
		}
		
		return out;
	}
	
	/**
	 * Proces an Concat model
	 * @param prnt, the parent of the Concat block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesConcat(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		ConcatModel out = new ConcatModel(parent);
		loadPosition(element, out);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		
		if (nl.size() > 0) {
			BlockModel left = procesBlockContent(out, nl.get(0), variables);
			out.setLeft(left);
		}
		if (nl.size() > 1) {
			BlockModel right = procesBlockContent(out, nl.get(1), variables);
			out.setRight(right);
		}
		
		return out;
	}
	
	/**
	 * Proces an CharAt model
	 * @param prnt, the parent of the CharAt block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesCharAt(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		CharAtModel out = new CharAtModel(parent);
		loadPosition(element, out);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		
		if (nl.size() > 0) {
			BlockModel left = procesBlockContent(out, nl.get(0), variables);
			out.setContent(left);
		}
		if (nl.size() > 1) {
			BlockModel right = procesBlockContent(out, nl.get(1), variables);
			out.setIndex(right);
		}
		
		return out;
	}
	
	/**
	 * Proces an show model
	 * @param prnt, the parent of the show block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesHide(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) {
		return new HideModel(parent);
	}

	/**
	 * Proces an random model
	 * @param prnt, the parent of the sleep block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesRandom(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) {
		RandomModel out = new RandomModel(parent);
		loadPosition(element, out);
		return out;
	}

	/**
	 * Proces an show model
	 * @param prnt, the parent of the sleep block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesShow(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) {
		return new ShowModel(parent);
	}

	/**
	 * Proces an move model
	 * @param prnt, the parent of the sleep block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesMove(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "move");
		MoveModel move = new MoveModel(parent);
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size() == 2){
			ArrayList<Element> nl1;
			checkNodeName(nl.get(0), "x");
			if((nl1 = trimNodeList(nl.get(0).getChildNodes())).size() == 1)
				move.setX(procesBlockContent(move, nl1.get(0), variables));
			checkNodeName(nl.get(1), "y");
			if((nl1 = trimNodeList(nl.get(1).getChildNodes())).size() == 1)
				move.setY(procesBlockContent(move, nl1.get(0), variables));	
		}else{
			throw new FileException("move should have 2 childeren");
		}
		loadPosition(element, move);
		return move;
	}

	/**
	 * Proces an sleep model
	 * @param prnt, the parent of the sleep block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private SleepModel procesSleep(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "sleep");
		
		SleepModel lock = new SleepModel(parent);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() > 0) {
			BlockModel left = procesBlockContent(lock, nl.get(0), variables);
			lock.setContent(left);
		}
		
		return lock;
	}
	
	/**
	 * Proces an lock model
	 * @param prnt, the parent of the lock block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private LockModel procesLock(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "lock");
		
		LockModel lock = new LockModel(parent);
		String name = element.getAttribute("name");
		
		if (!name.equals(""))
			lock.setVariable(variables.get(name).makeReference(lock));
		
		return lock;
	}
	
	/**
	 * Proces an unlock model
	 * @param prnt, the parent of the unlock block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private UnLockModel procesUnLock(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "unlock");
		
		UnLockModel lock = new UnLockModel(parent);
		String name = element.getAttribute("name");
		
		if (!name.equals(""))
			lock.setVariable(variables.get(name).makeReference(lock));
		
		return lock;
	}
	
	/**
	 * Proces an operator model
	 * @param prnt, the parent of the operator block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private BlockModel procesOperator(BlockModel parent, Element element,
			HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "operator");
		if (getBodyLength(element) != 2)
			throw new FileException("Operator body is not 2.");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		//unair
		if(nl.get(1).getNodeName().equals("dummy")){
			UnOperatorModel op = new UnOperatorModel(parent);
			op.setOperator(element.getAttribute("op"));
			BlockModel left = procesBlockContent(op, nl.get(0), variables);
			op.setLeft(left);
			return op;
		}else{
			//binaire
			OperatorModel op = new OperatorModel(parent);
			op.setOperator(element.getAttribute("op"));
			if (!nl.get(1).getNodeName().equals("NULL")) {
				BlockModel right = procesBlockContent(op, nl.get(1), variables);
				op.setRight(right);
			}
			if (!nl.get(0).getNodeName().equals("NULL")) {
				BlockModel left = procesBlockContent(op, nl.get(0), variables);
				op.setLeft(left);
			}
			loadPosition(element, op);
			return op;
		}
		

	}

	/**
	 * Proces an function call model
	 * @param prnt, the parent of the function call block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private FunctionCallModel procesFunctionCallModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "FunctionCall");
		
		FunctionCallModel ret = new FunctionCallModel(prnt, null);
		
		String name = element.getAttribute("name");
		_undefinedCalls.put(ret, name);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() < 1) throw new FileException("Function call doesnt contain arguments");
			procesFunctionCallParams(ret, nl.get(0), variables);
		if (nl.size() == 2)
			procesFunctionCallReturns(ret, nl.get(1), variables);
		
		return ret;
	}
	
	/**
	 * proces the parameter values from a function calll
	 * @param prnt, the functionCallModel
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private void procesFunctionCallParams(FunctionCallModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "params");
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				if(!nl.get(i).getNodeName().equals("NULL")){
					AbstractRefVariabelModel var = procesRefVariabelModel(prnt, (Element) nl.get(i), variables);
					prnt.addParam(var, i);
				}
								
			}
	}
	
	/**
	 * proces the return values from a function calll
	 * @param prnt, the functionCallModel
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private void procesFunctionCallReturns(FunctionCallModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "returns");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				if(!nl.get(i).getNodeName().equals("NULL")){
					AbstractRefVariabelModel var = procesRefVariabelModel(prnt, (Element) nl.get(i), variables);
					prnt.addreturn(var, i);
				}
			}
	}
	
	/**
	 * Proces an emit model
	 * @param prnt, the parent of the emit block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private EmitModel procesEmitModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "emit");
		EmitModel ret = new EmitModel(prnt);
		
		if (!element.getAttribute("name").equals("")) {
			ret.setEvent(_collection.getEventModel(element.getAttribute("name")).makeReference(prnt));
			_currentClass.addOutputEvent(ret.getEvent().getEventModel());
			ArrayList<Element> nl = trimNodeList(element.getChildNodes());
			if(nl.size()>0)
				for(int i=0; i<nl.size(); i++) {
					String name = ((Element) nl.get(i)).getAttribute("member");
					ret.setMember(name, procesRefVariabelModel(ret, (Element) nl.get(i), variables));
				}
		}

		return ret;
	}
	
	/**
	 * Proces an return model
	 * @param prnt, the parent of the return block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private ReturnModel procesReturnModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "return");
		
		ReturnModel ret = new ReturnModel(prnt);
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				ret.addReturnVar(procesRefVariabelModel(ret, (Element) nl.get(i), variables));
			}
		
		return ret;
	}
	
	/**
	 * Proces an set model
	 * @param prnt, the parent of the set block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private SetModel procesSetModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "setVar");
		
		String name = element.getAttribute("name");
		SetModel v = new SetModel(prnt);
		if (!name.equals(""))
			v.setVariable(variables.get(name).makeReference(v));
		
		if (getBodyLength(element) > 1) throw new FileException("Set body is larger than 1.");
		String[] namesList = { "value", "var", "length", "concat", "operator", "random", "charat", "arith", "access"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() >= 1) {
			Element child = nl.get(0);
			v.setContent(procesBlockContent(v, child, variables));
		}
		
		return v;
	}
	
	/**
	 * Proces an access model
	 * @param prnt, the parent of the access block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private AccessModel procesAccessModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "access");
		
		String name = element.getAttribute("name");
		AccessModel v = new AccessModel(prnt);
		v.setMember(name);

		loadPosition(element, v);
		return v;
	}	
	
	/**
	 * Proces an value model
	 * @param prnt, the parent of the value block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private ValueModel procesValueModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "value");
		
		NodeList nl = element.getChildNodes();
		String content = "";
		if (nl.getLength() >= 1) 
			content = nl.item(0).getNodeValue();
		ValueModel v = new ValueModel(prnt);
		v.setContent(content);
		loadPosition(element, v);
		return v;
	}	
	
	/**
	 * Proces an variable model
	 * @param prnt, the parent of the variable block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private VariableModel procesVariabelModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "makeVar");
		
		String name = element.getAttribute("name");
		String type = element.getAttribute("type");
		
		VariableModel m = new VariableModel(prnt, name, VariableFactory.getType(type));
		variables.put(name, m);
		return m;
	}
	
	/**
	 * Proces an ref variable model
	 * @param prnt, the parent of the ref variable block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private AbstractRefVariabelModel procesRefVariabelModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "var");
		
		String name = element.getAttribute("name");

		if (variables.get(name) == null) return null;
		return variables.get(name).makeReference(prnt);
	}
	
	/**
	 * Proces an print block
	 * @param prnt, the parent of the print block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private PrintModel procesPrintModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "print");
		PrintModel print = new PrintModel(prnt);
		
		if (getBodyLength(element) > 1) throw new FileException("Print body is larger than 1.");
		String[] namesList = { "value", "var", "length", "concat", "operator", "random", "charat", "arith", "access"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() > 0) {
			Element child = nl.get(0);
			print.setContent(procesBlockContent(print, child, variables));
		}
		
		return print;
	}
	
	/**
	 * Proces an appear block
	 * @param prnt, the parent of the print block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private ChangeAppearanceModel procesAppearModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "appear");
		ChangeAppearanceModel print = new ChangeAppearanceModel(prnt);
		
		if (getBodyLength(element) > 1) throw new FileException("Appear body is larger than 1.");
		String[] namesList = { "value", "var", "length", "concat", "operator", "random", "charat", "arith", "access"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if (nl.size() > 0) {
			Element child = nl.get(0);
			print.setContent(procesBlockContent(print, child, variables));
		}
		
		return print;
	}
	
	/**
	 * Proces an If block
	 * @param prnt, the parent of the if block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private IfBlockModel procesIfModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "if");
		IfBlockModel out = new IfBlockModel(prnt);
		
		if (getBodyLength(element) != 2) throw new FileException("If body is larger than 2.");
		String[] namesList = { "cond", "block"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		Element child = nl.get(0);
		if(child.getNodeName().equals("cond")){
			if ( trimNodeList(child.getChildNodes()).size() > 0)
				out.setCondition(procesBlockContent(out, trimNodeList(child.getChildNodes()).get(0), variables));
			child = nl.get(1);
			if(child.getNodeName().equals("block"))
				out.setBody(procesBlocks(nl.get(1), out, variables));
		}
		else{
			out.setBody(procesBlocks(nl.get(0), out, variables));
			child = nl.get(1);
			if(child.getNodeName().equals("cond") && trimNodeList(child.getChildNodes()).size() > 0)
				out.setCondition(procesBlockContent(out, trimNodeList(child.getChildNodes()).get(0), variables));
		}
		
		return out;
	}
	
	/**
	 * Proces an while block
	 * @param prnt, the parent of the while block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private WhileModel procesWhileModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "while");
		WhileModel out = new WhileModel(prnt);
		
		if (getBodyLength(element) != 2) throw new FileException("while body is larger than 2.");
		String[] namesList = { "cond", "block"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		Element child = nl.get(0);
		if(child.getNodeName().equals("cond")){
			if ( trimNodeList(child.getChildNodes()).size() > 0)
				out.setCondition(procesBlockContent(out, trimNodeList(child.getChildNodes()).get(0), variables));
			child = nl.get(1);
			if(child.getNodeName().equals("block"))
				out.setBody(procesBlocks(nl.get(1), out, variables));
		}
		else{
			out.setBody(procesBlocks(nl.get(0), out, variables));
			child = nl.get(1);
			if(child.getNodeName().equals("cond") && trimNodeList(child.getChildNodes()).size() > 0)
				out.setCondition(procesBlockContent(out, trimNodeList(child.getChildNodes()).get(0), variables));
		}
		
		return out;
	}
	
	/**
	 * Proces an forever block
	 * @param prnt, the parent of the forever block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private ForeverModel procesForeverModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "forever");
		ForeverModel out = new ForeverModel(prnt);
		
		if (getBodyLength(element) != 1) throw new FileException("forever body is larger than 1.");
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		out.setBody(procesBlocks(nl.get(0), out, variables));
		
		return out;
	}
	
	/**
	 * Proces an If block
	 * @param prnt, the parent of the if block
	 * @param element, the XML element
	 * @param variables, the defined variables
	 * @return the created model
	 * @throws FileException
	 */
	private IfElseModel procesIfElseModel(BlockModel prnt, Element element, HashMap<String, AbstractVariableModel> variables) throws FileException {
		checkNodeName(element, "if-else");
		IfElseModel out = new IfElseModel(prnt);
		
		if (getBodyLength(element) != 3) throw new FileException("If-else body is larger than 3.");
		String[] namesList = { "cond", "block"};
		checkBodyOnlyContains(element, arrayToCollection(namesList));
		
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		Element cond = nl.get(0);
		Element ifB = nl.get(1);
		Element elseB = nl.get(2);

		if ( trimNodeList(cond.getChildNodes()).size() > 0)
			out.setCondition(procesBlockContent(out, trimNodeList(cond.getChildNodes()).get(0), variables));
		out.setBodyIf(procesBlocks(ifB, out, variables));
		out.setBodyElse(procesBlocks(elseB, out, variables));
		
		return out;
	}
	
	/**
	 * Check if the element [element] has the expected name [name]
	 * @param element, the element
	 * @param name, the expected name of the element
	 * @throws FileException
	 */
	private void checkNodeName(Element element, String name) throws FileException {
		if (!element.getNodeName().equals(name)) throw new FileException("Body has invalid name: " + element.getNodeName());
	}
	
	/**
	 * Check if the element [element] has the expected name [name]
	 * @param element, the element
	 * @param name, the expected name of the element
	 * @throws FileException
	 */
	private void checkBodyNotContains(Element element, String name) throws FileException {
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) 
				if (nl.get(i).getNodeName().equals(name)) throw new FileException("Body contains invalid element: " + name);
	}
	
	/**
	 * Check if the body only contains the given list of names
	 * @param element, the element
	 * @param names, the expected names of the element
	 * @throws FileException
	 */
	private void checkBodyOnlyContains(Element element, ArrayList<String> names) throws FileException {
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		if(nl.size()>0)
			for(int i=0; i<nl.size(); i++) {
				String childName = nl.get(i).getNodeName();
				if (!names.contains(childName)) throw new FileException("Body contains invalid element: " + childName);
			
			}
	}
	
	/**
	 * Converts an array to an collection
	 * @param names
	 * @return the collection
	 */
	private ArrayList<String> arrayToCollection(String[] names) {
		ArrayList<String> ret = new ArrayList<String>();
		for(String str: names) ret.add(str);
		return ret;
	}
	
	/**
	 * Get the number of child nodes in the element [element]
	 * @param element, the XML element
	 * @return number of childs
	 */
	private int getBodyLength(Element element) {
		ArrayList<Element> nl = trimNodeList(element.getChildNodes());
		return nl.size();
	}
	
	/**
	 * Load the positions of an element
	 * @param item
	 * @param out
	 */
	private void loadPosition(Element item, BlockModel out) {
		try {
			out.setPos(new Point((int) Double.parseDouble(item.getAttribute("x")), (int) Double.parseDouble(item.getAttribute("y"))));
		} catch (NumberFormatException e) {
			out.setPos(new Point(0,0));
		}
	}

}
