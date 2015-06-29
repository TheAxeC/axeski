package ide.backend.runtime;

import ide.backend.exceptions.CompileException;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.classes.ClassModel;
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
import ide.backend.model.variables.MemberModel;
import ide.backend.model.variables.PrintModel;
import ide.backend.model.variables.RefMemberModel;
import ide.backend.model.variables.RefVariabelModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;

/**
 * Interface used to compile programs
 * @author Axel
 */
public interface Compiler<T> {

	/**
	 * The function used to compile programs
	 * @param runtime, the runtime containing the program
	 */
	public boolean compile(Runtime runtime, ModelCollection collection);
	
	/**
	 * The function used to compile programs
	 * @param runtime, the runtime containing the program
	 */
	public boolean compileDebug(Runtime runtime, ModelCollection collection);

	/**
	 * Compiles a value model
	 * @param model, the value model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ValueModel model) throws CompileException;
	
	/**
	 * Compiles a print model
	 * @param model, the print model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(PrintModel model) throws CompileException;
	
	/**
	 * Compiles a class model
	 * @param model, the class model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ClassModel model) throws CompileException;
	
	/**
	 * Compiles a connected model
	 * @param model, the connected model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ConnectedBlocks model) throws CompileException;
	
	/**
	 * Compiles a event model
	 * @param model, the event model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(EventModel model) throws CompileException;
	
	/**
	 * Compiles a function model
	 * @param model, the function model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(FunctionModel model) throws CompileException;
	
	/**
	 * Compiles a handler model
	 * @param model, the handler model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(HandlerModel model) throws CompileException;
	
	/**
	 * Compiles a refvariable model
	 * @param model, the refvariable model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(RefVariabelModel model) throws CompileException;
	
	/**
	 * Compiles a return model
	 * @param model, the return model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ReturnModel model) throws CompileException;
	
	/**
	 * Compiles a type model
	 * @param model, the type model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(TypeModel model) throws CompileException;
	
	/**
	 * Compiles a variable model
	 * @param model, the variable model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(VariableModel model) throws CompileException;
	
	/**
	 * Compiles a set model
	 * @param model, the set model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(SetModel model) throws CompileException;

	/**
	 * Compiles a RefEventModel.
	 * @param refEventModel
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(RefEventModel refEventModel) throws CompileException;

	/**
	 * Compiles an accessModel
	 * @param accessModel the access model to compile
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(AccessModel accessModel) throws CompileException;

	/**
	 * Compiles a functionCall model
	 * @param model, the functionCall model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(FunctionCallModel model) throws CompileException;
	
	/**
	 * Compiles a Emit model
	 * @param model, the Emit model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(EmitModel model) throws CompileException;

	/**
	 * Compiles an Instance Model.
	 * @param model, the Instance model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(InstanceModel model) throws CompileException;

	/**
	 * Compiles a wire Model.
	 * @param model, the wire model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(WireModel wireModel) throws CompileException;

	/**
	 * Compiles a wireFrame Model.
	 * @param model, the wireFrame model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(WireFrameModel wireFrameModel) throws CompileException;
	
	/**
	 * Compiles an Operator Model.
	 * @param model, the Operator model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(OperatorModel model) throws CompileException;

	/**
	 * Compiles an ifBlock Model.
	 * @param model, the ifBlock model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(IfBlockModel model) throws CompileException;

	/**
	 * Compiles an WhileModel Model.
	 * @param model, the WhileModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(WhileModel model) throws CompileException;

	/**
	 * Compiles a Member model
	 * @param model, the Member model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(MemberModel memberModel)throws CompileException;
	
	/**
	 * Compiles a RefMember model
	 * @param model, the RefMember model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(RefMemberModel model) throws CompileException;

	/**
	 * Compiles a ChangeAppearanceModel model
	 * @param model, the ChangeAppearanceModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ChangeAppearanceModel model) throws CompileException;

	/**
	 * Compiles a ForeverModel model
	 * @param model, the ForeverModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ForeverModel model) throws CompileException;
	
	/**
	 * Compiles a LockModel model
	 * @param model, the LockModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(LockModel model) throws CompileException;
	
	/**
	 * Compiles a LockModel model
	 * @param model, the LockModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(UnLockModel model) throws CompileException;
	
	/**
	 * Compiles a SleepModel model
	 * @param model, the SleepModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(SleepModel model) throws CompileException;


	/**
	 * Compiles a RandomModel model
	 * @param model, the RandomModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(RandomModel model) throws CompileException;

	/**
	 * Compiles a ShowModel model
	 * @param model, the ShowModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ShowModel model) throws CompileException;

	/**
	 * Compiles a MoveModel model
	 * @param model, the MoveModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(MoveModel moveModel) throws CompileException;

	/**
	 * Compiles a HideModel model
	 * @param model, the HideModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(HideModel model);
	
	/**
	 * Compiles a IfElseModel model
	 * @param model, the IfElseModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(IfElseModel model) throws CompileException;
	
	/**
	 * Compiles a Concat model
	 * @param model, the ConcatModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(ConcatModel model) throws CompileException;
	
	/**
	 * Compiles a LengthModel
	 * @param model, the LengthModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(LengthModel model) throws CompileException;
	
	/**
	 * Compiles a CharAt model
	 * @param model, the CharAtModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(CharAtModel model) throws CompileException;

	/**
	 * Compiles a UnOperator model
	 * @param model, the UnOperatorModel model
	 * @return the created execution object
	 * @throws CompileException
	 */
	public T compileBlock(UnOperatorModel unOperatorModel) throws CompileException;
	
}

