/**
 * Internal dependencies
 */
import { registerReducer } from 'calypso/state/redux-store';
import reducer from 'calypso/state/partner-portal/reducer';

registerReducer( [ 'partnerPortal' ], reducer );
