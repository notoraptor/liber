package liber.notification.info;

import liber.data.Contact;
import liber.notification.Info;

/** TODO: Gestion de la suppression � distanced'un contact.
 * Lorsqu'un contact coupe la relation, une option doit �tre propos�e au compte courant
 * pour sauvegarder l'historique de discussion dans un fichier donn�.
 * TODO: Gestion priv�e de la suppression d'un contact.
 * Id�alement, pour des questions de vie priv�e, un contact qui coupe une relation
 * peut ne pas vouloir que le compte courant sache qu'il a supprim� la relation.
 * Il peut donc �tre pr�f�rable de ne pas supprimer localement le contact, mais de se comporter constamment
 * comme si le contact distant �tait injoignable, c'est-�-dire:
 * * plus aucun message re�u de sa part.
 * * impossibilit� de lui envoyer des messages.
 * Mais dans un tel cas, il faut g�rer des situations plus complexes.
 * Par exemple, si le contact distant renvoie une demande de mise en relation pour recr�er le lien,
 * cette demande doit �tre automatiquement accept�e.
 */
public class ContactDeleted extends Info<Contact> {
	public ContactDeleted(Contact contact) {
		super(contact);
	}
}
