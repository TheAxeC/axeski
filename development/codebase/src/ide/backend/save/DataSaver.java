package ide.backend.save;


import ide.backend.exceptions.FileException;
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
import ide.backend.runtime.ModelCollection;

/**
 * Interface used to store a program in the IDE in a given FILE.
 * @author Matthijs Kaminski
 *
 */
public interface DataSaver <T> {
	
	/**
	 * Retuns all allowed file extensions.
	 * @return String[] containing all allow file extensions.
	 */
	public String[] getAllowedExtensions();
	
	/**
	 * Save the program to a file
	 * @param collection the program to save
	 * @param fileName the file to save to
	 * @throws FileException thrown if an error occurs
	 */
	public void save(ModelCollection collection, String fileName) throws FileException;
	
	/**
	 * Save the program to a string
	 * Images are not saved
	 * @param collection program to save
	 * @return the saved data
	 */
	public String save(ModelCollection collection);
	
	/**
	 * Saves a value model
	 * @param <T>
	 * @param model the value model
	 */
	public T saveBlock(ValueModel model);
	
	/**
	 * Saves a member model
	 * @param <T>
	 * @param model the value model
	 */
	public T saveBlock(MemberModel model);
	
	/**
	 * Saves a print model
	 * @param model the print model
	 */
	public T saveBlock(PrintModel model);
	
	/**
	 * Saves a class model
	 * @param model the class model
	 */
	public T saveBlock(ClassModel model);
	
	/**
	 * Saves a connected model
	 * @param model the connected model
	 */
	public T saveBlock(ConnectedBlocks model);
	
	/**
	 * Saves a event model
	 * @param model the event model
	 */
	public T saveBlock(EventModel model);
	
	/**
	 * Saves a function model
	 * @param model the function model
	 */
	public T saveBlock(FunctionModel model);
	
	/**
	 * Saves a handler model
	 * @param model the handler model
	 */
	public T saveBlock(HandlerModel model);
	
	/**
	 * Saves a refvariable model
	 * @param model the refvariable model
	 */
	public T saveBlock(RefVariabelModel model);
	
	/**
	 * Saves a return model
	 * @param model the return model
	 */
	public T saveBlock(ReturnModel model);
	
	/**
	 * Saves a type model
	 * @param model the type model
	 */
	public T saveBlock(TypeModel model);
	
	/**
	 * Saves variable model
	 * @param model the variable model

	 */
	public T saveBlock(VariableModel model);
	
	/**
	 * Saves a set model
	 * @param model the set model
	 */
	public T saveBlock(SetModel model);

	/**
	 * Saves a RefEventModel.
	 * @param refEventModel
	 */
	public T saveBlock(RefEventModel model);

	/**
	 * Saves a accessModel
	 * @param accessModel
	 */
	public T saveBlock(AccessModel model);
	
	/**
	 * Saves an emitModel
	 * @param model the emitModel
	 */
	public T saveBlock(EmitModel model);

	/**
	 * Saves an FunctionCallmodel
	 * @param functionCallModel
	 */
	public T saveBlock(FunctionCallModel model);

	/**
	 * Saves an InstanceModel
	 * @param instanceModel
	 */
	public T saveBlock(InstanceModel instanceModel);

	/**
	 * Saves an OperatorModel
	 * @param OperatorModel
	 */
	public T saveBlock(OperatorModel model);
	
	/** Saves an WireModel
	 * @param instanceModel
	 */
	public T saveBlock(WireModel model);

	/**
	 * Saves a wireFrameModel
	 * @param model
	 */
	public T saveBlock(WireFrameModel model);

	/**
	 * Saves an ifBlockModel
	 * @param model
	 * @return
	 */
	public T saveBlock(IfBlockModel model);
	
	/**
	 * Saves an ifElseModel
	 * @param model
	 * @return
	 */
	public T saveBlock(IfElseModel model);

	/**
	 * Saves an ifBlockModel
	 * @param model
	 * @return
	 */
	public T saveBlock(WhileModel model);

	/**
	 * Saves a RefMember model
	 * @param <T>
	 * @param model the RefMember model
	 */
	public T saveBlock(RefMemberModel model);

	/**
	 * Saves a ChangeAppearanceModel model
	 * @param <T>
	 * @param model the ChangeAppearanceModel model
	 */
	public T saveBlock(ChangeAppearanceModel model);

	/**
	 * Saves a ForeverModel model
	 * @param <T>
	 * @param model the ChangeAppearanceModel model
	 */
	public T saveBlock(ForeverModel model);

	/**
	 * Saves a MoveModel model
	 * @param <T>
	 * @param model the ChangeAppearanceModel model
	 */
	public T saveBlock(MoveModel model);
	
	/**
	 * Saves a LockModel model
	 * @param <T>
	 * @param model the LockModel model
	 */
	public T saveBlock(LockModel model);
	
	/**
	 * Saves a LockModel model
	 * @param <T>
	 * @param model the LockModel model
	 */
	public T saveBlock(UnLockModel model);
	
	/**
	 * Saves a SleepModel model
	 * @param <T>
	 * @param model the SleepModel model
	 */
	public T saveBlock(SleepModel model);

	/**
	 * Saves a RandomModel model
	 * @param <T>
	 * @param model the RandomModel model
	 */
	public T saveBlock(RandomModel model);

	/**
	 * Saves a ShowModel model
	 * @param <T>
	 * @param model the ShowModel model
	 */
	public T saveBlock(ShowModel model);

	/**
	 * Saves a HideModel model
	 * @param <T>
	 * @param model the HideModel model
	 */
	public T saveBlock(HideModel model);
	
	/**
	 * Saves a ConcatModel model
	 * @param <T>
	 * @param model the ConcatModel model
	 */
	public T saveBlock(ConcatModel model);
	
	
	/**
	 * Saves a LengthModel model
	 * @param <T>
	 * @param model the LengthModel model
	 */
	public T saveBlock(LengthModel model);
	
	/**
	 * Saves a CharAtModel model
	 * @param <T>
	 * @param model the CharAtModel model
	 */
	public T saveBlock(CharAtModel model);

	/**
	 * Saves a UnOperatorModel model
	 * @param <T>
	 * @param model the CharAtModel model
	 */
	public T saveBlock(UnOperatorModel model);
}
